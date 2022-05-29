package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.list.ListItem;
import cz.mg.collections.map.Map;
import cz.mg.vulkantransformator.entities.preprocessor.Definition;
import cz.mg.vulkantransformator.utilities.code.Token;

import static cz.mg.vulkantransformator.services.parser.preprocessor.Directives.*;

public @Service class Preprocessor {
    private static final String DIRECTIVE = "#";

    private static Preprocessor instance;

    public static Preprocessor getInstance() {
        if (instance == null) {
            instance = new Preprocessor();
            instance.defineParser = DefineParser.getInstance();
            instance.errorParser = ErrorParser.getInstance();
        }
        return instance;
    }

    private DefineParser defineParser;
    private ErrorParser errorParser;

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
            String directive = getDirective(tokens);
            if (directive != null) {
                if (exclude) {
                    if (directive.equals(ENDIF)) {
                        exclude = false;
                    }
                } else {
                    if (directive.equals(INCLUDE)) {
                        // skip includes
                    } else if (directive.equals(IFDEF)) {
                        if (!defined(map, tokens.get(2).getText())) {
                            exclude = true;
                        }
                    } else if (directive.equals(IFNDEF)) {
                        if (defined(map, tokens.get(2).getText())) {
                            exclude = true;
                        }
                    } else if (directive.equals(ENDIF)) {
                        // skip
                    } else if (directive.equals(DEFINE)) {
                        Definition definition = defineParser.parse(tokens);
                        definitions.addLast(definition);
                        map.set(definition.getName().getText(), definition);
                    } else if (directive.equals(UNDEF)) {
                        removeDefinition(definitions, tokens.get(2).getText());
                        map = createMap(definitions);
                    } else if (directive.equals(ERROR)) {
                        errorParser.parse(tokens);
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
                            // TODO - only replacing simple definitions for now
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

    private @Optional String getDirective(@Mandatory List<Token> tokens) {
        if (tokens.count() >= 2) {
            if (tokens.getFirst().getText().equals(DIRECTIVE)) {
                return tokens.getFirstItem().getNextItem().get().getText();
            }
        }

        return null;
    }

    private boolean defined(@Mandatory Map<String, Definition> map, @Mandatory String name) {
        return map.getOptional(name) != null;
    }
}
