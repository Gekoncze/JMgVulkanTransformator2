package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkVariable;
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
 * VKAPI_ATTR VkResult VKAPI_CALL vkCreateInstance(
 * const VkInstanceCreateInfo*                 pCreateInfo,
 * const VkAllocationCallbacks*                pAllocator,
 * VkInstance*                                 pInstance)
 */
public @Service class VkFunctionParser implements VkParser {
    private static VkFunctionParser instance;

    public static @Mandatory VkFunctionParser getInstance() {
        if (instance == null) {
            instance = new VkFunctionParser();
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

    private VkFunctionParser() {
    }

    @Override
    public boolean matches(@Mandatory Statement statement) {
        return patternMatcher.matches(
            statement,
            false,
            Matchers.text("VKAPI_ATTR"),
            Matchers.type(TokenType.NAME),
            Matchers.text("VKAPI_CALL"),
            Matchers.type(TokenType.NAME),
            Matchers.text("(")
        );
    }

    @Override
    public @Mandatory VkFunction parse(@Mandatory Statement statement) {
        List<Token> tokens = new List<>(statement.getTokens());

        tokenRemover.removeFirst(tokens, "VKAPI_ATTR");

        VkFunction function = new VkFunction();

        function.setOutput(new VkVariable(
            tokenRemover.removeFirst(tokens, TokenType.NAME).getText(),
            0, "", 0
        ));

        tokenRemover.removeFirst(tokens, "VKAPI_CALL");

        function.setName(tokenRemover.removeFirst(tokens, TokenType.NAME).getText());

        tokenRemover.removeFirst(tokens, "(");
        tokenRemover.removeLast(tokens, ")");

        List<Statement> parameterStatements = statementParser.parse(tokens, ",");
        for (Statement parameterStatement : parameterStatements) {
            if (variableParser.matches(parameterStatement)) {
                function.getInput().addLast(variableParser.parse(parameterStatement));
            } else {
                throw new ParseException(parameterStatement.getTokens().getFirst(), "Illegal parameter declaration.");
            }
        }

        return function;
    }
}
