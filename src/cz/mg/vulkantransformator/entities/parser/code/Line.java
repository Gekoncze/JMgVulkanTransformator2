package cz.mg.vulkantransformator.entities.parser.code;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;

public @Entity class Line {
    private Integer id;
    private String text;

    public Line() {
    }

    public Line(Integer id, String text) {
        this.id = id;
        this.text = text;
    }

    @Required
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Required
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
