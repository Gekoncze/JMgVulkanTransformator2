package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.Parser;
import cz.mg.c.parser.entities.CFile;
import cz.mg.c.preprocessor.processors.macro.entities.Macro;
import cz.mg.c.preprocessor.processors.macro.entities.Macros;
import cz.mg.collections.services.StringJoiner;
import cz.mg.tokenizer.entities.Position;
import cz.mg.tokenizer.entities.tokens.WordToken;
import cz.mg.tokenizer.exceptions.CodeException;
import cz.mg.tokenizer.services.PositionService;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;

public @Service class VulkanParser {
    private static volatile @Service VulkanParser instance;

    public static @Service VulkanParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VulkanParser();
                    instance.parser = Parser.getInstance();
                    instance.converter = VulkanConverter.getInstance();
                    instance.stringJoiner = StringJoiner.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service Parser parser;
    private @Service VulkanConverter converter;
    private @Service StringJoiner stringJoiner;

    private VulkanParser() {
    }

    public @Mandatory VkRoot parse(@Mandatory File file) {
        try {
            Macros macros = new Macros();
            macros.getDefinitions().addLast(createEmptyMacro("VKAPI_PTR"));
            macros.getDefinitions().addLast(createEmptyMacro("VKAPI_ATTR"));
            macros.getDefinitions().addLast(createEmptyMacro("VKAPI_CALL"));
            CFile cFile = parser.parse(convert(file), macros);
            return converter.convert(cFile, macros);
        } catch (CodeException e) {
            Position position = PositionService.getInstance().find(convert(file).getContent(), e.getPosition());
            throw new CodeException(
                e.getPosition(),
                "At row " + position.getRow() + " column " + position.getColumn() + ": " + e.getMessage(),
                e
            );
        }
    }

    private @Mandatory Macro createEmptyMacro(@Mandatory String name) {
        Macro macro = new Macro();
        macro.setName(new WordToken(name, -1));
        return macro;
    }

    private @Mandatory cz.mg.file.File convert(@Mandatory File file) {
        return new cz.mg.file.File(
            file.getPath(),
            stringJoiner.join(file.getLines(), "\n")
        );
    }
}
