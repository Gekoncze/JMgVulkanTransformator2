package cz.mg.vulkantransformator.utilities.code;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;

public @Utility class Line {
    private final int id;
    private final @Mandatory String text;

    public Line(int id, @Mandatory String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public @Mandatory String getText() {
        return text;
    }
}
