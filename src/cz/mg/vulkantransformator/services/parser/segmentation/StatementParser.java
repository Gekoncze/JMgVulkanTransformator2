package cz.mg.vulkantransformator.services.parser.segmentation;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.entities.parser.code.Statement;
import cz.mg.vulkantransformator.entities.parser.code.Token;

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
        return parse(tokens, ";");
    }

    public @Mandatory List<Statement> parse(@Mandatory List<Token> tokens, @Mandatory String delimiter) {
        List<Statement> statements = new List<>();
        Statement statement = new Statement();

        int nestingCount = 0;

        for (Token token : tokens) {
            if (!token.getText().equals(delimiter) || nestingCount != 0) {
                statement.getTokens().addLast(token);
            }

            if (isOpeningCurlyBracket(token)) {
                nestingCount++;
            } else if (isClosingCurlyBracket(token)) {
                nestingCount--;
            } else if (isDelimiter(token, delimiter)) {
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
            throw new ParseException(statements.getLast().getTokens().getLast(), "Missing right curly bracket.");
        }

        if (nestingCount < 0) {
            throw new ParseException(statements.getLast().getTokens().getLast(), "Missing left curly bracket.");
        }

        return statements;
    }

    private boolean isOpeningCurlyBracket(@Mandatory Token token) {
        return token.getText().equals("{");
    }

    private boolean isClosingCurlyBracket(@Mandatory Token token) {
        return token.getText().equals("}");
    }

    private boolean isDelimiter(@Mandatory Token token, @Mandatory String delimiter) {
        return token.getText().equals(delimiter);
    }
}
