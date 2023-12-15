package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.preprocessor.processors.macro.components.MacroExpander;
import cz.mg.c.preprocessor.processors.macro.components.MacroManager;
import cz.mg.c.preprocessor.processors.macro.entities.Macro;
import cz.mg.c.preprocessor.processors.macro.entities.Macros;
import cz.mg.collections.list.List;
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

    public @Mandatory List<VkConstant> convert(@Mandatory Macros macros) {
        MacroManager macroManager = new MacroManager(macros);
        List<VkConstant> constants = new List<>();
        for (Macro macro : macros.getDefinitions()) {
            if (isConstant(macro)) {
                constants.addLast(convert(macro, macroManager));
            }
        }
        return constants;
    }

    private boolean isConstant(@Mandatory Macro macro) {
        return macro.getParameters() == null && !macro.getTokens().isEmpty();
    }

    private @Mandatory VkConstant convert(@Mandatory Macro macro, @Mandatory MacroManager macroManager) {
        VkConstant constant = create(macro, macroManager);
        constant.setName(macro.getName().getText());
        constant.setValue(joiner.join(macro.getTokens(), "", Token::getText));
        return constant;
    }

    private @Mandatory VkConstant create(@Mandatory Macro macro, @Mandatory MacroManager macroManager) {
        List<Token> expandedTokens = MacroExpander.expand(macro.getTokens(), macroManager);

        boolean number = false;
        boolean decimal = false;
        boolean text = false;

        for (Token token : expandedTokens) {
            if (token instanceof NumberToken) {
                number = true;
                if (token.getText().contains(".")) {
                    decimal = true;
                }
            }

            if (token instanceof DoubleQuoteToken) {
                text = true;
            }
        }

        if (text) {
            return new VkStringConstant();
        } else if (number && decimal) {
            return new VkFloatConstant();
        } else if (number) {
            return new VkIntegerConstant();
        } else {
            throw new CodeException(
                macro.getName().getPosition(),
                "Unsupported constant " + macro.getName().getText() + "."
            );
        }
    }
}
