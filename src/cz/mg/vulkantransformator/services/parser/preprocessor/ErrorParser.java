package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.utilities.code.Token;

public @Service class ErrorParser {
    private static @Optional ErrorParser instance;

    public static @Mandatory ErrorParser getInstance() {
        if (instance == null) {
            instance = new ErrorParser();
            instance.tokenRemover = TokenRemover.getInstance();
        }
        return instance;
    }

    private TokenRemover tokenRemover;

    private ErrorParser() {
    }

    public void parse(@Mandatory List<Token> tokens) {
        parseError(new List<>(tokens));
    }

    private void parseError(@Mandatory List<Token> tokens) {
        tokenRemover.removeFirst(tokens, "#");
        Token token = tokenRemover.removeFirst(tokens, "error");

        if (!tokens.isEmpty()) {
            throw new ParseException(token, "Error directive reached: \"" + tokens.get(2).getText() + "\".");
        } else {
            throw new ParseException(token, "Error directive reached.");
        }
    }
}
