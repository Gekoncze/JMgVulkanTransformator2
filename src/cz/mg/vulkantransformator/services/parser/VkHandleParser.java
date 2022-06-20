package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkHandle;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcher;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.Token;

/**
 * Defined as:
 *
 * VK_DEFINE_HANDLE(VkQueue)
 * VK_DEFINE_NON_DISPATCHABLE_HANDLE(VkSemaphore)
 *
 * #define VK_DEFINE_HANDLE(object) typedef struct object##_T* object;
 * #define VK_DEFINE_NON_DISPATCHABLE_HANDLE(object) typedef uint64_t object;
 *
 * Examples:
 *
 * typedef struct VkQueue_T* VkQueue
 * typedef uint64_t VkQueue
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
        throw new UnsupportedOperationException("TODO"); // TODO
    }

    @Override
    public @Mandatory VkHandle parse(@Mandatory Statement statement) {
        // TODO - currently not working - needs support from preprocessor
        List<Token> tokens = new List<>(statement.getTokens());

        throw new UnsupportedOperationException("TODO"); // TODO
    }
}
