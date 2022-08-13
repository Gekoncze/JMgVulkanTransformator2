package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkFunctionPointer;
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
 * typedef void (VKAPI_PTR *PFN_vkFreeFunction)(
 * void*                                       pUserData,
 * void*                                       pMemory);
 */
public @Service class VkFunctionPointerParser implements VkParser {
    private static VkFunctionPointerParser instance;

    public static @Mandatory VkFunctionPointerParser getInstance() {
        if (instance == null) {
            instance = new VkFunctionPointerParser();
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

    private VkFunctionPointerParser() {
    }

    @Override
    public boolean matches(@Mandatory Statement statement) {
        return patternMatcher.matches(
            statement,
            false,
            Matchers.text("typedef"),
            Matchers.type(TokenType.NAME),
            Matchers.text("("),
            Matchers.text("VKAPI_PTR"),
            Matchers.text("*"),
            Matchers.type(TokenType.NAME),
            Matchers.text(")"),
            Matchers.text("(")
        ) || patternMatcher.matches(
            statement,
            false,
            Matchers.text("typedef"),
            Matchers.type(TokenType.NAME),
            Matchers.text("*"),
            Matchers.text("("),
            Matchers.text("VKAPI_PTR"),
            Matchers.text("*"),
            Matchers.type(TokenType.NAME),
            Matchers.text(")"),
            Matchers.text("(")
        );
    }

    @Override
    public @Mandatory VkFunctionPointer parse(@Mandatory Statement statement) {
        List<Token> tokens = new List<>(statement.getTokens());

        tokenRemover.removeFirst(tokens, "typedef");

        VkFunctionPointer functionPointer = new VkFunctionPointer();

        String outputTypename = tokenRemover.removeFirst(tokens, TokenType.NAME).getText();
        String token = tokenRemover.removeFirst(tokens).getText();
        int pointers = 0;

        if (token.equals("*")) {
            pointers++;
            tokenRemover.removeFirst(tokens, "(");
        }

        functionPointer.setOutput(new VkVariable(outputTypename, pointers, "", 0));

        tokenRemover.removeFirst(tokens, "VKAPI_PTR");
        tokenRemover.removeFirst(tokens, "*");

        functionPointer.setName(tokenRemover.removeFirst(tokens, TokenType.NAME).getText());

        tokenRemover.removeFirst(tokens, ")");
        tokenRemover.removeFirst(tokens, "(");
        tokenRemover.removeLast(tokens, ")");

        List<Statement> parameterStatements = statementParser.parse(tokens, ",");
        for (Statement parameterStatement : parameterStatements) {
            if (variableParser.matches(parameterStatement)) {
                functionPointer.getInput().addLast(variableParser.parse(parameterStatement));
            } else if (isVoid(parameterStatement)){
                //noinspection UnnecessaryContinue
                continue;
            } else {
                throw new ParseException(parameterStatement.getTokens().getFirst(), "Illegal parameter declaration.");
            }
        }

        return functionPointer;
    }

    private boolean isVoid(@Mandatory Statement parameterStatement) {
        return parameterStatement.getTokens().count() == 1
            && parameterStatement.getTokens().getFirst().getText().equals("void");
    }
}
