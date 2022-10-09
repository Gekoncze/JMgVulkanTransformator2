package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.entities.parser.code.Token;

import static cz.mg.vulkantransformator.services.parser.preprocessor.Directives.ERROR;

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
        Token token = tokenRemover.removeFirst(tokens, ERROR);

        if (!tokens.isEmpty()) {
            String message = token.getLine().getText().substring(
                tokens.getFirst().getBeginId(),
                tokens.getLast().getEndId()
            );
            throw new ParseException(token, "Error directive reached: " + message + ".");
        } else {
            throw new ParseException(token, "Error directive reached.");
        }
    }
}
