package cz.mg.vulkantransformator.services.parser.matcher;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.vulkantransformator.utilities.code.TokenType;

public @Utility class Matchers {
    public static @Mandatory Matcher any() {
        return token -> true;
    }

    public static @Mandatory Matcher text(@Mandatory String text) {
        return token -> token.getText().equals(text);
    }

    public static @Mandatory Matcher type(@Mandatory TokenType type) {
        return token -> token.getType().equals(type);
    }
}
