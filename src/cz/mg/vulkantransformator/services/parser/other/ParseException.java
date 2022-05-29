package cz.mg.vulkantransformator.services.parser.other;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.vulkantransformator.utilities.code.Line;
import cz.mg.vulkantransformator.utilities.code.Token;

public class ParseException extends RuntimeException {
    private final @Optional Token token;
    private final @Optional Line line;

    public ParseException(@Optional Token token, String message) {
        super(getLocation(token) + message);
        this.token = token;
        this.line = null;
    }

    public ParseException(@Optional Line line, String message) {
        super(getLocation(line) + message);
        this.token = null;
        this.line = line;
    }

    public @Optional Token getToken() {
        return token;
    }

    public @Optional Line getLine() {
        return line;
    }

    private static @Mandatory String getLocation(@Optional Token token) {
        if (token != null) {
            return getLocation(token.getLine());
        } else {
            return "";
        }
    }

    private static @Mandatory String getLocation(@Optional Line line) {
        if (line != null) {
            return "Error at line " + line.getId() + ": ";
        } else {
            return "";
        }
    }
}
