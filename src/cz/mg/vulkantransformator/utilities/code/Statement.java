package cz.mg.vulkantransformator.utilities.code;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;

public @Utility class Statement {
    private final @Mandatory List<Token> tokens = new List<>();

    public Statement() {
    }

    public @Mandatory List<Token> getTokens() {
        return tokens;
    }
}
