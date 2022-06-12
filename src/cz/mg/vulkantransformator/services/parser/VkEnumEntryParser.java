package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.vulkantransformator.entities.vulkan.VkEnumEntry;
import cz.mg.vulkantransformator.services.parser.matcher.Matchers;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcher;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.TokenType;

/**
 * Example:
 *
 *     VK_STENCIL_FACE_FRONT_BIT = 0x00000001,
 *     VK_STENCIL_FACE_BACK_BIT = 0x00000002,
 *     VK_STENCIL_FACE_FRONT_AND_BACK = 0x00000003,
 *     VK_STENCIL_FRONT_AND_BACK = VK_STENCIL_FACE_FRONT_AND_BACK,
 *     VK_STENCIL_FACE_FLAG_BITS_MAX_ENUM = 0x7FFFFFFF
 */
public @Service class VkEnumEntryParser implements VkParser {
    private static @Optional VkEnumEntryParser instance;

    public static @Mandatory VkEnumEntryParser getInstance() {
        if (instance == null) {
            instance = new VkEnumEntryParser();
            instance.patternMatcher = PatternMatcher.getInstance();
            instance.tokenRemover = TokenRemover.getInstance();
        }
        return instance;
    }

    private PatternMatcher patternMatcher;
    private TokenRemover tokenRemover;

    private VkEnumEntryParser() {
    }

    @Override
    public boolean matches(@Mandatory Statement statement) {
        return patternMatcher.matches(
            statement,
            Matchers.type(TokenType.NAME)
        );
    }

    @Override
    public @Mandatory VkEnumEntry parse(@Mandatory Statement statement) {
        throw new UnsupportedOperationException("TODO"); // TODO
    }
}
