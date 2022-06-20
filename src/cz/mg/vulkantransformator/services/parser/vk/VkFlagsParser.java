package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkFlags;
import cz.mg.vulkantransformator.services.parser.matcher.Matchers;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcher;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

/**
 * Example:
 *
 * typedef VkFlags VkInstanceCreateFlags
 */
public @Service class VkFlagsParser implements VkParser {
    private static @Optional VkFlagsParser instance;

    public static @Mandatory VkFlagsParser getInstance() {
        if (instance == null) {
            instance = new VkFlagsParser();
            instance.patternMatcher = PatternMatcher.getInstance();
            instance.tokenRemover = TokenRemover.getInstance();
        }
        return instance;
    }

    private PatternMatcher patternMatcher;
    private TokenRemover tokenRemover;

    private VkFlagsParser() {
    }


    @Override
    public boolean matches(@Mandatory Statement statement) {
        return patternMatcher.matches(
            statement,
            true,
            Matchers.text("typedef"),
            Matchers.text("VkFlags"),
            Matchers.type(TokenType.NAME)
        );
    }

    @Override
    public @Mandatory VkFlags parse(@Mandatory Statement statement) {
        List<Token> tokens = new List<>(statement.getTokens());

        tokenRemover.removeFirst(tokens, "typedef");
        tokenRemover.removeFirst(tokens, "VkFlags");

        VkFlags flags = new VkFlags();
        flags.setName(tokenRemover.removeFirst(tokens, TokenType.NAME).getText());

        tokenRemover.verifyNoMoreTokens(tokens);

        return flags;
    }
}
