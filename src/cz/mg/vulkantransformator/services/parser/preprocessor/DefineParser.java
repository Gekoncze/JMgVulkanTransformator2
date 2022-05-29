package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.preprocessor.Definition;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.utilities.code.Token;

public @Service class DefineParser { // TODO - add test
    private static @Optional DefineParser instance;

    public static @Mandatory DefineParser getInstance() {
        if (instance == null) {
            instance = new DefineParser();
        }
        return instance;
    }

    private DefineParser() {
    }

    public @Mandatory Definition parse(@Mandatory List<Token> tokens) {
        if (tokens.count() < 3) {
            throw new ParseException(tokens.getFirst(), "Missing define directive name.");
        }

        Definition definition = new Definition();
        definition.setName(tokens.get(2));

        // TODO - only processing simple definitions for now
        if (tokens.count() == 4) {
            definition.getExpression().addLast(tokens.get(3));
        }

        return definition;
    }
}
