package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.preprocessor.processors.macro.entities.Macro;
import cz.mg.collections.services.StringJoiner;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.DoubleQuoteToken;
import cz.mg.tokenizer.entities.tokens.NumberToken;
import cz.mg.tokenizer.exceptions.CodeException;
import cz.mg.vulkantransformator.entities.vulkan.VkConstant;
import cz.mg.vulkantransformator.entities.vulkan.VkFloatConstant;
import cz.mg.vulkantransformator.entities.vulkan.VkIntegerConstant;
import cz.mg.vulkantransformator.entities.vulkan.VkStringConstant;

public @Service class VkConstantConverter {
    private static volatile @Service VkConstantConverter instance;

    public static @Service VkConstantConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkConstantConverter();
                    instance.joiner = StringJoiner.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service StringJoiner joiner;

    private VkConstantConverter() {
    }

    public boolean matches(@Mandatory Macro macro) {
        return macro.getParameters() == null && !macro.getTokens().isEmpty();
    }

    public @Mandatory VkConstant convert(@Mandatory Macro macro) {
        String value = joiner.join(macro.getTokens(), "", Token::getText);
        VkConstant constant = create(macro, value);
        constant.setName(macro.getName().getText());
        constant.setValue(value);
        return constant;
    }

    private @Mandatory VkConstant create(@Mandatory Macro macro, @Mandatory String value) {
        if (isString(macro, value)) {
            return new VkStringConstant();
        } else if (isInteger(macro, value)) {
            return new VkIntegerConstant();
        } else if (isFloat(macro, value)) {
            return new VkFloatConstant();
        } else {
            throw new CodeException(
                macro.getName().getPosition(),
                "Unsupported constant " + macro.getName().getText() + "."
            );
        }
    }

    private boolean isInteger(@Mandatory Macro macro, @Mandatory String value)
    {
        return macro.getTokens().containsMatch(token -> token instanceof NumberToken)
            && !value.contains(".");
    }

    private boolean isFloat(@Mandatory Macro macro, @Mandatory String value)
    {
        return macro.getTokens().containsMatch(token -> token instanceof NumberToken)
            && value.contains(".");
    }

    private boolean isString(@Mandatory Macro macro, @Mandatory String value)
    {
        return macro.getTokens().containsMatch(token -> token instanceof DoubleQuoteToken);
    }
}
