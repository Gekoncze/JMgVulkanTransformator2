package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.vulkantransformator.utilities.code.Token;

public class ParseException extends RuntimeException {
    private final @Mandatory Token token;

    public ParseException(@Mandatory Token token, String message) {
        super("Error at line " + token.getLine().getId() + ": " + message);
        this.token = token;
    }

    public @Mandatory Token getToken() {
        return token;
    }
}
