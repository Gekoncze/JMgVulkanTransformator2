package cz.mg.vulkantransformator.services.parser.other;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.vulkantransformator.entities.parser.code.Line;
import cz.mg.vulkantransformator.entities.parser.code.Token;

public class ParseException extends RuntimeException {
    private final @Optional Token token;
    private final @Optional Line line;

    public ParseException(@Optional Token token, @Mandatory String message) {
        super(getLocation(token) + message);
        this.token = token;
        this.line = null;
    }

    public ParseException(@Optional Line line, @Mandatory String message) {
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
            return "Error at line " + token.getLine().getId() + " column " + token.getBeginId() + ": ";
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
