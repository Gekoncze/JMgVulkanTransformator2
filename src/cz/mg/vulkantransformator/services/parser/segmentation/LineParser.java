package cz.mg.vulkantransformator.services.parser.segmentation;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.parser.code.Line;

public @Service class LineParser {
    private static @Optional LineParser instance;

    public static @Mandatory LineParser getInstance() {
        if (instance == null) {
            instance = new LineParser();
        }
        return instance;
    }

    private LineParser() {
    }

    public @Mandatory List<Line> parse(@Mandatory List<String> stringLines) {
        List<Line> lines = new List<>();

        int id = 0;
        for (String stringLine : stringLines) {
            lines.addLast(new Line(id, stringLine));
            id++;
        }

        return lines;
    }
}
