package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkUnion;
import cz.mg.vulkantransformator.services.parser.matcher.Matchers;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcher;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.services.parser.segmentation.StatementParser;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

/**
 * Example:
 *
 * typedef union VkClearColorValue {
 *     float       float32[4];
 *     int32_t     int32[4];
 *     uint32_t    uint32[4];
 * } VkClearColorValue;
 */
public @Service class VkUnionParser implements VkParser {
    private static @Optional VkUnionParser instance;

    public static @Mandatory VkUnionParser getInstance() {
        if (instance == null) {
            instance = new VkUnionParser();
            instance.patternMatcher = PatternMatcher.getInstance();
            instance.statementParser = StatementParser.getInstance();
            instance.tokenRemover = TokenRemover.getInstance();
            instance.fieldParser = VkFieldParser.getInstance();
        }
        return instance;
    }

    private PatternMatcher patternMatcher;
    private StatementParser statementParser;
    private TokenRemover tokenRemover;
    private VkFieldParser fieldParser;

    private VkUnionParser() {
    }

    @Override
    public boolean matches(@Mandatory Statement statement) {
        return patternMatcher.matches(
            statement,
            Matchers.text("typedef"),
            Matchers.text("union"),
            Matchers.any(),
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
            union.getFields().addLast(fieldParser.parse(fieldStatement));
        }

        return union;
    }
}
