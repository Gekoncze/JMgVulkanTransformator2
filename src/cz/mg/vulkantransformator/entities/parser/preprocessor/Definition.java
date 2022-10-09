package cz.mg.vulkantransformator.entities.parser.preprocessor;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.parser.code.Token;

public @Entity class Definition {
    private Token name;
    private List<Token> parameters = new List<>();
    private List<Token> expression = new List<>();

    public Definition() {
    }

    public Definition(Token name, List<Token> parameters, List<Token> expression) {
        this.name = name;
        this.parameters = parameters;
        this.expression = expression;
    }

    @Required @Value
    public Token getName() {
        return name;
    }

    public void setName(Token name) {
        this.name = name;
    }

    @Required @Part
    public List<Token> getParameters() {
        return parameters;
    }

    public void setParameters(List<Token> parameters) {
        this.parameters = parameters;
    }

    @Required @Part
    public List<Token> getExpression() {
        return expression;
    }

    public void setExpression(List<Token> expression) {
        this.expression = expression;
    }
}
