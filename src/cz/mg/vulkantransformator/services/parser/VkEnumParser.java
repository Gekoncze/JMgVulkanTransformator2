package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkEnum;
import cz.mg.vulkantransformator.services.parser.matcher.Matchers;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcher;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.services.parser.segmentation.StatementParser;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

/**
 * Example:
 *
 * typedef enum VkStencilFaceFlagBits {
 *     VK_STENCIL_FACE_FRONT_BIT = 0x00000001,
 *     VK_STENCIL_FACE_BACK_BIT = 0x00000002,
 *     VK_STENCIL_FACE_FRONT_AND_BACK = 0x00000003,
 *     VK_STENCIL_FRONT_AND_BACK = VK_STENCIL_FACE_FRONT_AND_BACK,
 *     VK_STENCIL_FACE_FLAG_BITS_MAX_ENUM = 0x7FFFFFFF
 * } VkStencilFaceFlagBits;
 */
public @Service class VkEnumParser implements VkParser {
    private static @Optional VkEnumParser instance;

    public static @Mandatory VkEnumParser getInstance() {
        if (instance == null) {
            instance = new VkEnumParser();
            instance.patternMatcher = PatternMatcher.getInstance();
            instance.statementParser = StatementParser.getInstance();
            instance.tokenRemover = TokenRemover.getInstance();
            instance.entryParser = VkEnumEntryParser.getInstance();
        }
        return instance;
    }

    private PatternMatcher patternMatcher;
    private StatementParser statementParser;
    private TokenRemover tokenRemover;
    private VkEnumEntryParser entryParser;

    private VkEnumParser() {
    }

    @Override
    public boolean matches(@Mandatory Statement statement) {
        return patternMatcher.matches(
            statement,
            Matchers.text("typedef"),
            Matchers.text("enum"),
            Matchers.type(TokenType.NAME),
            Matchers.text("{")
        );
    }

    @Override
    public @Mandatory VkEnum parse(@Mandatory Statement statement) {
        List<Token> tokens = new List<>(statement.getTokens());

        tokenRemover.removeFirst(tokens, "typedef");
        tokenRemover.removeFirst(tokens, "enum");

        VkEnum vkenum = new VkEnum();
        vkenum.setName(tokenRemover.removeFirst(tokens, TokenType.NAME).getText());

        tokenRemover.removeFirst(tokens, "{");

        tokenRemover.removeLast(tokens, vkenum.getName());
        tokenRemover.removeLast(tokens, "}");

        List<Statement> entryStatements = statementParser.parse(tokens, ",");
        for (Statement entryStatement : entryStatements) {
            if (entryParser.matches(entryStatement)) {
                vkenum.getEntries().addLast(entryParser.parse(statement));
            } else {
                throw new ParseException(entryStatement.getTokens().getFirst(), "Illegal enum entry declaration.");
            }
        }

        return vkenum;
    }
}
