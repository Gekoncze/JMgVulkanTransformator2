package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.preprocessor.processors.macro.components.MacroExpander;
import cz.mg.c.preprocessor.processors.macro.components.MacroManager;
import cz.mg.c.entities.macro.Macro;
import cz.mg.c.entities.macro.Macros;
import cz.mg.collections.components.StringJoiner;
import cz.mg.collections.list.List;
import cz.mg.token.Token;
import cz.mg.token.tokens.quote.DoubleQuoteToken;
import cz.mg.token.tokens.NumberToken;
import cz.mg.tokenizer.exceptions.TraceableException;
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
                }
            }
        }
        return instance;
    }

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
        constant.setValue(new StringJoiner<>(macro.getTokens()).withConverter(Token::getText).join());
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
            throw new TraceableException(
                macro.getName().getPosition(),
                "Unsupported constant " + macro.getName().getText() + "."
            );
        }
    }
}