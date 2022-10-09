package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkStructure;
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
 * typedef struct VkPipelineColorBlendStateCreateInfo {
 *     VkStructureType                               sType;
 *     const void*                                   pNext;
 *     VkPipelineColorBlendStateCreateFlags          flags;
 *     VkBool32                                      logicOpEnable;
 *     VkLogicOp                                     logicOp;
 *     uint32_t                                      attachmentCount;
 *     const VkPipelineColorBlendAttachmentState*    pAttachments;
 *     float                                         blendConstants[4];
 * } VkPipelineColorBlendStateCreateInfo
 */
public @Service class VkStructureParser implements VkParser {
    private static VkStructureParser instance;

    public static @Mandatory VkStructureParser getInstance() {
        if (instance == null) {
            instance = new VkStructureParser();
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

    private VkStructureParser() {
    }

    @Override
    public boolean matches(@Mandatory Statement statement) {
        return patternMatcher.matches(
            statement,
            false,
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.type(TokenType.NAME),
            Matchers.text("{")
        );
    }

    @Override
    public @Mandatory VkStructure parse(@Mandatory Statement statement) {
        List<Token> tokens = new List<>(statement.getTokens());

        tokenRemover.removeFirst(tokens, "typedef");
        tokenRemover.removeFirst(tokens, "struct");

        VkStructure structure = new VkStructure();
        structure.setName(tokenRemover.removeFirst(tokens, TokenType.NAME).getText());

        tokenRemover.removeFirst(tokens, "{");

        tokenRemover.removeLast(tokens, structure.getName());
        tokenRemover.removeLast(tokens, "}");

        List<Statement> fieldStatements = statementParser.parse(tokens);
        for (Statement fieldStatement : fieldStatements) {
            if (variableParser.matches(fieldStatement)) {
                structure.getFields().addLast(variableParser.parse(fieldStatement));
            } else {
                throw new ParseException(fieldStatement.getTokens().getFirst(), "Illegal field declaration.");
            }
        }

        return structure;
    }
}
