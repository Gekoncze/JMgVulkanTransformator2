package cz.mg.vulkantransformator.entities.parser.code;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.collections.list.List;

public @Entity class Statement {
    private List<Token> tokens = new List<>();

    public Statement() {
    }

    @Required
    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }
}
