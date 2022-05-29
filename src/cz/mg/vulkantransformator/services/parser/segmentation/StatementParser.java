package cz.mg.vulkantransformator.services.parser.segmentation;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.Token;

public @Service class StatementParser {
    private static @Optional StatementParser instance;

    public static @Mandatory StatementParser getInstance() {
        if (instance == null) {
            instance = new StatementParser();
        }
        return instance;
    }

    private StatementParser() {
    }

    public @Mandatory List<Statement> parse(@Mandatory List<Token> tokens) {
        List<Statement> statements = new List<>();
        Statement statement = new Statement();

        int nestingCount = 0;

        for (Token token : tokens) {
            if (!token.getText().equals(";") || nestingCount != 0) {
                statement.getTokens().addLast(token);
            }

            if (isOpeningCurlyBracket(token)) {
                nestingCount++;
            } else if (isClosingCurlyBracket(token)) {
                nestingCount--;
            } else if (isSemicolon(token)) {
                if (nestingCount == 0) {
                    if (!statement.getTokens().isEmpty()) {
                        statements.addLast(statement);
                        statement = new Statement();
                    }
                }
            }
        }

        if (!statement.getTokens().isEmpty()) {
            statements.addLast(statement);
        }

        if (nestingCount > 0) {
            throw new RuntimeException("Missing right curly bracket at line " + tokens.getLast().getLine().getId() + ".");
        }

        if (nestingCount < 0) {
            throw new RuntimeException("Missing left curly bracket at line " + tokens.getLast().getLine().getId() + ".");
        }

        return statements;
    }

    private boolean isOpeningCurlyBracket(@Mandatory Token token) {
        return token.getText().equals("{");
    }

    private boolean isClosingCurlyBracket(@Mandatory Token token) {
        return token.getText().equals("}");
    }

    private boolean isSemicolon(@Mandatory Token token) {
        return token.getText().equals(";");
    }
}
