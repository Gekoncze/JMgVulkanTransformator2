package cz.mg.vulkantransformator.services.parser.other;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.utilities.code.Token;

@SuppressWarnings("UnusedReturnValue")
public @Service class TokenRemover {
    private static @Optional TokenRemover instance;

    public static @Mandatory TokenRemover getInstance() {
        if (instance == null) {
            instance = new TokenRemover();
        }
        return instance;
    }

    private TokenRemover() {
    }

    public @Mandatory Token removeFirst(@Mandatory List<Token> tokens, @Mandatory String expectation) {
        Token token = tokens.removeFirst();
        validate(token, expectation);
        return token;
    }

    public @Mandatory Token removeLast(@Mandatory List<Token> tokens, @Mandatory String expectation) {
        Token token = tokens.removeLast();
        validate(token, expectation);
        return token;
    }

    private void validate(@Mandatory Token token, @Mandatory String expectation) {
        if (!token.getText().equals(expectation)) {
            throw new ParseException(token, "Expected '" + expectation + "', but got '" + token.getText() + "'.");
        }
    }
}
