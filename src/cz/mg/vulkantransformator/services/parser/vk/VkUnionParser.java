package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkUnion;
import cz.mg.vulkantransformator.services.parser.matcher.Matchers;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcher;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.services.parser.segmentation.StatementParser;
import cz.mg.vulkantransformator.entities.parser.code.Statement;
import cz.mg.vulkantransformator.entities.parser.code.Token;
import cz.mg.vulkantransformator.entities.parser.code.TokenType;

/**
 * Example:
 *
 * typedef union VkClearColorValue {
 *     float       float32[4];
 *     int32_t     int32[4];
 *     uint32_t    uint32[4];
 * } VkClearColorValue
 */
public @Service class VkUnionParser implements VkParser {
    private static @Optional VkUnionParser instance;

    public static @Mandatory VkUnionParser getInstance() {
        if (instance == null) {
            instance = new VkUnionParser();
            instance.patternMatcher = PatternMatcher.getInstance();
            instance.statementParser = StatementParser.getInstance();
            instance.tokenRemover = TokenRemover.getInstance();
            instance.variableParser = VkVariableParser.getInstance();
        }
        return instance;
    }

    private PatternMatcher patternMatcher;
    private StatementParser statementParser;
    private TokenRemover tokenRemover;
    private VkVariableParser variableParser;

    private VkUnionParser() {
    }

    @Override
    public boolean matches(@Mandatory Statement statement) {
        return patternMatcher.matches(
            statement,
            false,
            Matchers.text("typedef"),
            Matchers.text("union"),
            Matchers.type(TokenType.NAME),
            Matchers.text("{")
        );
    }

    @Override
    public @Mandatory VkUnion parse(@Mandatory Statement statement) {
        List<Token> tokens = new List<>(statement.getTokens());

        tokenRemover.removeFirst(tokens, "typedef");
        tokenRemover.removeFirst(tokens, "union");

        VkUnion union = new VkUnion(tokenRemover.removeFirst(tokens, TokenType.NAME).getText());

        tokenRemover.removeFirst(tokens, "{");

        tokenRemover.removeLast(tokens, union.getName());
        tokenRemover.removeLast(tokens, "}");

        List<Statement> fieldStatements = statementParser.parse(tokens);

        for (Statement fieldStatement : fieldStatements) {
            if (variableParser.matches(fieldStatement)) {
                union.getFields().addLast(variableParser.parse(fieldStatement));
            } else {
                throw new ParseException(fieldStatement.getTokens().getFirst(), "Illegal field declaration.");
            }
        }

        return union;
    }
}
