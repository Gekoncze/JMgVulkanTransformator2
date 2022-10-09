package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.parser.preprocessor.Definition;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.services.parser.other.TokenValidator;
import cz.mg.vulkantransformator.entities.parser.code.Token;
import cz.mg.vulkantransformator.entities.parser.code.TokenType;

import static cz.mg.vulkantransformator.services.parser.preprocessor.Directives.*;

public @Service class Preprocessor {
    private static final String DIRECTIVE = "#";

    private static Preprocessor instance;

    public static Preprocessor getInstance() {
        if (instance == null) {
            instance = new Preprocessor();
            instance.validator = TokenValidator.getInstance();
            instance.defineParser = DefineParser.getInstance();
            instance.errorParser = ErrorParser.getInstance();
            instance.tokenPreprocessor = TokenPreprocessor.getInstance();
        }
        return instance;
    }

    private TokenValidator validator;
    private DefineParser defineParser;
    private ErrorParser errorParser;
    private TokenPreprocessor tokenPreprocessor;

    private Preprocessor() {
    }

    public @Mandatory List<Token> preprocess(
        @Mandatory List<List<Token>> linesTokens,
        @Mandatory List<Definition> definitionList
    ) {
        return preprocess(linesTokens, new DefinitionManager(definitionList));
    }

    private @Mandatory List<Token> preprocess(
        @Mandatory List<List<Token>> linesTokens,
        @Mandatory DefinitionManager definitions
    ) {
        List<Token> remainingTokens = new List<>();

        boolean exclude = false;

        for (List<Token> tokens : linesTokens) {
            String directive = findDirective(tokens);
            if (directive != null) {
                if (exclude) {
                    if (directive.equals(ELIF)) {
                        // skip for now
                    } else if (directive.equals(ELSE)) {
                        // skip for now
                    } else if (directive.equals(ENDIF)) {
                        exclude = false;
                    }
                } else {
                    if (directive.equals(INCLUDE)) {
                        // skip includes
                    } else if (directive.equals(IF)) {
                        // skip for now
                    } else if (directive.equals(ELIF)) {
                        // skip for now
                    } else if (directive.equals(ELSE)) {
                        // skip for now
                    } else if (directive.equals(IFDEF)) {
                        exclude = !definitions.defined(tokens.get(2).getText());
                    } else if (directive.equals(IFNDEF)) {
                        exclude = definitions.defined(tokens.get(2).getText());
                    } else if (directive.equals(ENDIF)) {
                        // skip
                    } else if (directive.equals(DEFINE)) {
                        definitions.define(defineParser.parse(tokens));
                    } else if (directive.equals(UNDEF)) {
                        definitions.undefine(tokens.get(2).getText());
                    } else if (directive.equals(ERROR)) {
                        errorParser.parse(tokens);
                    } else {
                        throw new ParseException(
                            tokens.get(1),
                            "Unexpected preprocessor directive '" + tokens.get(1).getText() +"'."
                        );
                    }
                }
            } else {
                if (!exclude) {
                    remainingTokens.addCollectionLast(
                        tokenPreprocessor.preprocess(tokens, definitions)
                    );
                }
            }
        }

        return remainingTokens;
    }

    private @Optional String findDirective(@Mandatory List<Token> tokens) {
        if (tokens.count() >= 2) {
            if (tokens.getFirst().getText().equals(DIRECTIVE)) {
                Token token = tokens.getFirstItem().getNextItem().get();
                validator.validate(token, TokenType.NAME);
                return token.getText();
            }
        }

        return null;
    }
}
