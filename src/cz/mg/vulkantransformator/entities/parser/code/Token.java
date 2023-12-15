package cz.mg.vulkantransformator.entities.parser.code;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;

@Deprecated
public @Entity class Token {
    private Line line;
    private Integer beginId;
    private Integer endId;
    private TokenType type;
    private String text;

    public Token() {
    }

    public Token(Line line, Integer beginId, Integer endId, TokenType type, String text) {
        this.line = line;
        this.beginId = beginId;
        this.endId = endId;
        this.type = type;
        this.text = text;
    }

    @Required
    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    @Required
    public Integer getBeginId() {
        return beginId;
    }

    public void setBeginId(Integer beginId) {
        this.beginId = beginId;
    }

    @Required
    public Integer getEndId() {
        return endId;
    }

    public void setEndId(Integer endId) {
        this.endId = endId;
    }

    @Required
    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    @Required
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
