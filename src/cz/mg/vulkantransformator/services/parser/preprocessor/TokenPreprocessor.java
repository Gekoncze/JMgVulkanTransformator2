package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.parser.preprocessor.Definition;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.entities.parser.code.Line;
import cz.mg.vulkantransformator.entities.parser.code.Token;
import cz.mg.vulkantransformator.entities.parser.code.TokenType;

public @Service class TokenPreprocessor {
    private static @Optional TokenPreprocessor instance;

    public static @Mandatory TokenPreprocessor getInstance() {
        if (instance == null) {
            instance = new TokenPreprocessor();
            instance.tokenRemover = TokenRemover.getInstance();
        }
        return instance;
    }

    private TokenRemover tokenRemover;

    private TokenPreprocessor() {
    }

    public @Mandatory List<Token> preprocess(@Mandatory List<Token> lineTokens, @Mandatory DefinitionManager definitions) {
        List<Token> tokens = new List<>(lineTokens);
        List<Token> preprocessedTokens = new List<>();

        if (tokens.isEmpty()) {
            return preprocessedTokens;
        }

        while (!tokens.isEmpty()) {
            Token token = tokens.removeFirst();
            Definition definition = definitions.get(token.getText());

            if (definition == null) {
                preprocessedTokens.addLast(token);
            } else {
                if (definition.getParameters().count() == 0) {
                    preprocessedTokens.addCollectionLast(definition.getExpression());
                } else if (definition.getParameters().count() == 1) {
                    // only supporting simple definition calls for now
                    tokenRemover.removeFirst(tokens, "(");
                    preprocessedTokens.addCollectionLast(
                        applyArgument(definition, tokenRemover.removeFirst(tokens, TokenType.NAME))
                    );
                    tokenRemover.removeFirst(tokens, ")");
                } else {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }
        }

        return concatenate(preprocessedTokens, lineTokens.getFirst().getLine().getId());
    }

    private @Mandatory List<Token> applyArgument(@Mandatory Definition definition, @Mandatory Token argument) {
        List<Token> tokens = new List<>();
        Token parameter = definition.getParameters().getFirst();
        for (Token token : definition.getExpression()) {
            if (token.getText().equals(parameter.getText())) {
                tokens.addLast(argument);
            } else {
                tokens.addLast(token);
            }
        }
        return tokens;
    }

    private @Mandatory List<Token> concatenate(@Mandatory List<Token> tokens, int lineId) {
        List<Token> concatenatedTokens = new List<>();
        int hashtagCount = 0;
        while (!tokens.isEmpty()) {
            Token token = tokenRemover.removeFirst(tokens);

            if (token.getText().equals("#")) {
                hashtagCount++;
            } else {
                concatenatedTokens.addLast(token);

                if (hashtagCount > 0) {
                    if (hashtagCount == 2) {
                        hashtagCount = 0;
                        Token second = tokenRemover.removeLast(concatenatedTokens);
                        Token first = tokenRemover.removeLast(concatenatedTokens);
                        concatenatedTokens.addLast(concatenate(first, second, lineId));
                    } else {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                }
            }
        }
        return concatenatedTokens;
    }

    private @Mandatory Token concatenate(@Mandatory Token first, @Mandatory Token second, int lineId) {
        if (first.getType() != second.getType()) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        // this is a bit hackish, but ok for now
        String text = first.getText() + second.getText();
        return new Token(new Line(lineId, text), 0, text.length(), first.getType(), text);
    }
}
