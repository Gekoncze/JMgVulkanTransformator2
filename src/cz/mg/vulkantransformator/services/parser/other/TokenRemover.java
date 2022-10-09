package cz.mg.vulkantransformator.services.parser.other;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.parser.code.Token;
import cz.mg.vulkantransformator.entities.parser.code.TokenType;

@SuppressWarnings("UnusedReturnValue")
public @Service class TokenRemover {
    private static @Optional TokenRemover instance;

    public static @Mandatory TokenRemover getInstance() {
        if (instance == null) {
            instance = new TokenRemover();
            instance.validator = TokenValidator.getInstance();
        }
        return instance;
    }

    private TokenValidator validator;

    private TokenRemover() {
    }

    public @Mandatory Token removeFirst(@Mandatory List<Token> tokens) {
        if (!tokens.isEmpty()) {
            return tokens.removeFirst();
        } else {
            throw new ParseException((Token) null, "Missing token.");
        }
    }

    public @Mandatory Token removeLast(@Mandatory List<Token> tokens) {
        if (!tokens.isEmpty()) {
            return tokens.removeLast();
        } else {
            throw new ParseException((Token) null, "Missing token.");
        }
    }

    public @Mandatory Token removeFirst(@Mandatory List<Token> tokens, @Mandatory String expectation) {
        Token token = removeFirst(tokens);
        validator.validate(token, expectation);
        return token;
    }

    public @Mandatory Token removeFirst(@Mandatory List<Token> tokens, @Mandatory TokenType expectation) {
        Token token = removeFirst(tokens);
        validator.validate(token, expectation);
        return token;
    }

    public @Mandatory Token removeLast(@Mandatory List<Token> tokens, @Mandatory String expectation) {
        Token token = removeLast(tokens);
        validator.validate(token, expectation);
        return token;
    }

    public @Mandatory Token removeLast(@Mandatory List<Token> tokens, @Mandatory TokenType expectation) {
        Token token = removeLast(tokens);
        validator.validate(token, expectation);
        return token;
    }

    public void verifyNoMoreTokens(@Mandatory List<Token> tokens) {
        if (!tokens.isEmpty()) {
            throw new ParseException(
                tokens.getFirst(),
                "Unprocessed token '" + tokens.getFirst().getText() + "'."
            );
        }
    }
}
