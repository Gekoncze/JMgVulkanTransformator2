package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkType;
import cz.mg.vulkantransformator.services.parser.matcher.Matchers;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcher;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

/**
 * Example:
 *
 * typedef uint64_t VkQueue
 */
public @Service class VkTypeParser implements VkParser {
    private static @Optional VkTypeParser instance;

    public static @Mandatory VkTypeParser getInstance() {
        if (instance == null) {
            instance = new VkTypeParser();
            instance.patternMatcher = PatternMatcher.getInstance();
            instance.tokenRemover = TokenRemover.getInstance();
        }
        return instance;
    }

    private PatternMatcher patternMatcher;
    private TokenRemover tokenRemover;

    private VkTypeParser() {
    }

    @Override
    public boolean matches(@Mandatory Statement statement) {
        return patternMatcher.matches(
            statement,
            true,
            Matchers.text("typedef"),
            Matchers.oneOf(
                Matchers.text("uint64_t"),
                Matchers.text("uint32_t")
            ),
            Matchers.type(TokenType.NAME)
        );
    }

    @Override
    public @Mandatory VkType parse(@Mandatory Statement statement) {
        List<Token> tokens = new List<>(statement.getTokens());

        tokenRemover.removeFirst(tokens, "typedef");
        tokenRemover.removeFirst(tokens, TokenType.NAME);

        VkType type = new VkType();
        type.setName(tokenRemover.removeFirst(tokens, TokenType.NAME).getText());

        tokenRemover.verifyNoMoreTokens(tokens);

        return type;
    }
}
