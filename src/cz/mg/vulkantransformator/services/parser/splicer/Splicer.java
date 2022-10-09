package cz.mg.vulkantransformator.services.parser.splicer;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.parser.code.Token;

public @Service class Splicer {
    private static final String CONTINUATION = "\\";

    private static @Optional Splicer instance;

    public static @Mandatory Splicer getInstance() {
        if (instance == null) {
            instance = new Splicer();
        }
        return instance;
    }

    private Splicer() {
    }

    public @Mandatory List<List<Token>> splice(@Mandatory List<List<Token>> linesTokens) {
        List<List<Token>> joinedLinesTokens = new List<>();

        for (List<Token> lineTokens : linesTokens) {
            if (isContinuation(joinedLinesTokens)) {
                joinedLinesTokens.getLast().removeLast();
                joinedLinesTokens.getLast().addCollectionLast(lineTokens);
            } else {
                joinedLinesTokens.addLast(new List<>(lineTokens));
            }
        }

        return joinedLinesTokens;
    }

    private boolean isContinuation(@Mandatory List<List<Token>> joinedLinesTokens) {
        if (!joinedLinesTokens.isEmpty()) {
            if (!joinedLinesTokens.getLast().isEmpty()) {
                return joinedLinesTokens.getLast().getLast().getText().equals(CONTINUATION);
            }
        }

        return false;
    }
}
