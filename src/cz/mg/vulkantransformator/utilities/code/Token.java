package cz.mg.vulkantransformator.utilities.code;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;

public @Utility class Token {
    private final @Mandatory Line line;
    private final int beginId;
    private final int endId;
    private @Optional String text;

    public Token(@Mandatory Line line, int beginId, int endId) {
        this.line = line;
        this.beginId = beginId;
        this.endId = endId;
    }

    public @Mandatory Line getLine() {
        return line;
    }

    public int getBeginId() {
        return beginId;
    }

    public int getEndId() {
        return endId;
    }

    public @Mandatory String getText() {
        if (text == null) {
            text = line.getText().substring(beginId, endId);
        }
        return text;
    }
}
