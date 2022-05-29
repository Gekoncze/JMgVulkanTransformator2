package cz.mg.vulkantransformator.services.parser.other;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

public @Service class TokenValidator { // TODO - add test
    private static @Optional TokenValidator instance;

    public static @Mandatory TokenValidator getInstance() {
        if (instance == null) {
            instance = new TokenValidator();
        }
        return instance;
    }

    private TokenValidator() {
    }

    public void validate(@Mandatory Token token, @Mandatory String expectation) {
        if (!token.getText().equals(expectation)) {
            throw new ParseException(
                token,
                "Expected '" + expectation + "', but got '" + token.getText() + "'."
            );
        }
    }

    public void validate(@Mandatory Token token, @Mandatory TokenType expectation) {
        if (!token.getType().equals(expectation)) {
            throw new ParseException(
                token,
                "Expected token of type '" + expectation.name() + "', but got '" + token.getType().name() + "'."
            );
        }
    }
}
