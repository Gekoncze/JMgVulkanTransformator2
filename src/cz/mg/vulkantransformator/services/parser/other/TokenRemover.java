package cz.mg.vulkantransformator.services.parser.other;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

@SuppressWarnings("UnusedReturnValue")
public @Service class TokenRemover { // TODO - add test
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

    public @Mandatory Token removeFirst(@Mandatory List<Token> tokens, @Mandatory String expectation) {
        Token token = tokens.removeFirst();
        validator.validate(token, expectation);
        return token;
    }

    public @Mandatory Token removeFirst(@Mandatory List<Token> tokens, @Mandatory TokenType expectation) {
        Token token = tokens.removeFirst();
        validator.validate(token, expectation);
        return token;
    }

    public @Mandatory Token removeLast(@Mandatory List<Token> tokens, @Mandatory String expectation) {
        Token token = tokens.removeLast();
        validator.validate(token, expectation);
        return token;
    }

    public @Mandatory Token removeLast(@Mandatory List<Token> tokens, @Mandatory TokenType expectation) {
        Token token = tokens.removeLast();
        validator.validate(token, expectation);
        return token;
    }
}
