package cz.mg.vulkantransformator.services.preprocessor;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.list.ListItem;
import cz.mg.collections.map.Map;
import cz.mg.vulkantransformator.entities.preprocessor.Definition;
import cz.mg.vulkantransformator.services.parser.TokenParser;
import cz.mg.vulkantransformator.utilities.code.Token;

public @Service class Preprocessor {
    private static Preprocessor instance;

    public static Preprocessor getInstance() {
        if (instance == null) {
            instance = new Preprocessor();
            instance.parser = TokenParser.getInstance();
        }
        return instance;
    }

    private TokenParser parser;

    private Preprocessor() {
    }

    public @Mandatory List<Token> preprocess(
        @Mandatory List<List<Token>> linesTokens,
        @Mandatory List<Definition> definitions
    ) {
        List<Token> remainingTokens = new List<>();
        Map<String, Definition> map = createMap(definitions);

        boolean exclude = false;

        // might want to add support for more directives
        for (List<Token> tokens : linesTokens) {
            if (isDirective(tokens)) {
                if (exclude) {
                    if (isEndif(tokens)) {
                        exclude = false;
                    }
                } else {
                    if (isInclude(tokens)) {
                        // skip includes
                    } else if (isIfdef(tokens)) {
                        if (!defined(map, tokens.get(2).getText())) {
                            exclude = true;
                        }
                    } else if (isIfndef(tokens)) {
                        if (defined(map, tokens.get(2).getText())) {
                            exclude = true;
                        }
                    } else if (isEndif(tokens)) {
                        // skip
                    } else if (isDefine(tokens)) {
                        Definition definition = parseDefinition(tokens);
                        if (definition != null) {
                            definitions.addLast(definition);
                            map.set(definition.getName().getText(), definition);
                        }
                    } else if (isUndef(tokens)) {
                        removeDefinition(definitions, tokens.get(2).getText());
                        map = createMap(definitions);
                    } else if (isError(tokens)) {
                        if (tokens.count() == 3) {
                            throw new RuntimeException("Error directive reached: \"" + tokens.get(2).getText() + "\".");
                        } else {
                            throw new RuntimeException("Error directive reached.");
                        }
                    } else {
                        throw new UnsupportedOperationException(
                            "Unsupported preprocessor directive '" + tokens.get(1).getText() +"'" +
                                " at line " + tokens.getFirst().getLine().getId() + "."
                        );
                    }
                }
            } else {
                if (!exclude) {
                    for (Token token : tokens) {
                        Definition definition = map.getOptional(token.getText());
                        if (definition == null) {
                            remainingTokens.addLast(token);
                        } else {
                            // only replacing simple definitions for now
                            remainingTokens.addLast(definition.getExpression().getFirst());
                        }
                    }
                }
            }
        }

        return remainingTokens;
    }

    private @Mandatory Map<String, Definition> createMap(@Mandatory List<Definition> definitions) {
        Map<String, Definition> map = new Map<>(100);

        for (Definition definition : definitions) {
            map.set(definition.getName().getText(), definition);
        }

        return map;
    }

    private void removeDefinition(@Mandatory List<Definition> definitions, @Mandatory String name) {
        for (
            ListItem<Definition> definitionItem = definitions.getFirstItem();
            definitionItem != null;
            definitionItem = definitionItem.getNextItem()
        ) {
            if (definitionItem.get().getName().getText().equals(name)) {
                definitions.remove(definitionItem);
            }
        }
    }

    private boolean isDirective(@Mandatory List<Token> tokens) {
        if (tokens.count() >= 2) {
            return tokens.getFirst().getText().equals("#");
        } else {
            return false;
        }
    }

    private boolean isInclude(@Mandatory List<Token> tokens) {
        return tokens.get(1).getText().equals("include");
    }

    private boolean isIfdef(@Mandatory List<Token> tokens) {
        return tokens.get(1).getText().equals("ifdef");
    }

    private boolean isIfndef(@Mandatory List<Token> tokens) {
        return tokens.get(1).getText().equals("ifndef");
    }

    private boolean isDefine(@Mandatory List<Token> tokens) {
        return tokens.get(1).getText().equals("define");
    }

    private boolean isUndef(@Mandatory List<Token> tokens) {
        return tokens.get(1).getText().equals("undef");
    }

    private boolean isEndif(@Mandatory List<Token> tokens) {
        return tokens.get(1).getText().equals("endif");
    }

    private boolean isError(@Mandatory List<Token> tokens) {
        return tokens.get(1).getText().equals("error");
    }

    private boolean defined(@Mandatory Map<String, Definition> map, @Mandatory String name) {
        return map.getOptional(name) != null;
    }

    private @Optional Definition parseDefinition(@Mandatory List<Token> tokens) {
        // only parsing simple definitions for now
        if (tokens.count() >= 3 && tokens.count() <= 4) {
            Definition definition = new Definition();

            definition.setName(tokens.get(2));

            if (tokens.count() >= 4) {
                definition.getExpression().addLast(tokens.get(3));
            }

            return definition;
        } else {
            return null;
        }
    }
}
