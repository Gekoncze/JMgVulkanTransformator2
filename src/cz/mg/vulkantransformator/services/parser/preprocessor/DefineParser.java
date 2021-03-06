package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.preprocessor.Definition;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

import static cz.mg.vulkantransformator.services.parser.preprocessor.Directives.DEFINE;
import static java.lang.Character.isWhitespace;

public @Service class DefineParser {
    private static @Optional DefineParser instance;

    public static @Mandatory DefineParser getInstance() {
        if (instance == null) {
            instance = new DefineParser();
            instance.tokenRemover = TokenRemover.getInstance();
        }
        return instance;
    }

    private TokenRemover tokenRemover;

    private DefineParser() {
    }

    public @Mandatory Definition parse(@Mandatory List<Token> tokens) {
        return parseDefinition(new List<>(tokens));
    }

    private @Mandatory Definition parseDefinition(@Mandatory List<Token> tokens) {
        tokenRemover.removeFirst(tokens, "#");
        tokenRemover.removeFirst(tokens, DEFINE);

        Definition definition = new Definition();
        definition.setName(tokenRemover.removeFirst(tokens, TokenType.NAME));

        if (!tokens.isEmpty()) {
            if (tokens.getFirst().getText().equals("(")) {
                char charBeforeBracket = tokens.getFirst().getLine().getText().charAt(
                    tokens.getFirst().getBeginId() - 1
                );

                if (!isWhitespace(charBeforeBracket)) {
                    definition.setParameters(removeParameters(tokens));
                }
            }

            definition.setExpression(tokens);
        }

        return definition;
    }

    @SuppressWarnings("UnnecessaryContinue")
    private @Mandatory List<Token> removeParameters(@Mandatory List<Token> tokens) {
        tokenRemover.removeFirst(tokens, "(");
        List<Token> parameters = new List<>();
        while (true) {
            parameters.addLast(tokenRemover.removeFirst(tokens, TokenType.NAME));

            Token token = tokenRemover.removeFirst(tokens, TokenType.SPECIAL);
            if (token.getText().equals(",")) {
                continue;
            } else if (token.getText().equals(")")) {
                break;
            } else {
                throw new ParseException(
                    token,
                    "Invalid define directive. Expected ',' or ')', but got '" + token.getText() + "'."
                );
            }
        }
        return parameters;
    }
}
