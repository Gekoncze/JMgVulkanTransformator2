package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkType;
import cz.mg.vulkantransformator.services.parser.matcher.Matchers;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcher;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.entities.parser.code.Statement;
import cz.mg.vulkantransformator.entities.parser.code.Token;
import cz.mg.vulkantransformator.entities.parser.code.TokenType;

/**
 * Example:
 *
 * typedef struct VkQueue_T* VkQueue
 */
public @Service class VkHandleParser implements VkParser {
    private static @Optional VkHandleParser instance;

    public static @Mandatory VkHandleParser getInstance() {
        if (instance == null) {
            instance = new VkHandleParser();
            instance.patternMatcher = PatternMatcher.getInstance();
            instance.tokenRemover = TokenRemover.getInstance();
        }
        return instance;
    }

    private PatternMatcher patternMatcher;
    private TokenRemover tokenRemover;

    private VkHandleParser() {
    }

    @Override
    public boolean matches(@Mandatory Statement statement) {
        return patternMatcher.matches(
            statement,
            true,
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.type(TokenType.NAME),
            Matchers.text("*"),
            Matchers.type(TokenType.NAME)
        );
    }

    @Override
    public @Mandatory VkType parse(@Mandatory Statement statement) {
        List<Token> tokens = new List<>(statement.getTokens());

        tokenRemover.removeFirst(tokens, "typedef");
        tokenRemover.removeFirst(tokens, "struct");
        tokenRemover.removeFirst(tokens, TokenType.NAME);
        tokenRemover.removeFirst(tokens, "*");

        VkType type = new VkType();
        type.setName(tokenRemover.removeFirst(tokens, TokenType.NAME).getText());

        tokenRemover.verifyNoMoreTokens(tokens);

        return type;
    }
}
