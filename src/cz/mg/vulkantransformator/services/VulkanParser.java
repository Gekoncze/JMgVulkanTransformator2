package cz.mg.vulkantransformator.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.CParser;
import cz.mg.c.entities.CFile;
import cz.mg.c.entities.macro.Macro;
import cz.mg.c.entities.macro.Macros;
import cz.mg.file.File;
import cz.mg.token.Position;
import cz.mg.token.tokens.WordToken;
import cz.mg.tokenizer.exceptions.TraceableException;
import cz.mg.tokenizer.services.PositionService;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.converter.VulkanConverter;

public @Service class VulkanParser {
    private static volatile @Service VulkanParser instance;

    public static @Service VulkanParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VulkanParser();
                    instance.converter = VulkanConverter.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service VulkanConverter converter;

    private VulkanParser() {
    }

    public @Mandatory VkRoot parse(@Mandatory File file) {
        try {
            Macros macros = new Macros();
            macros.getDefinitions().addLast(createEmptyMacro("VKAPI_PTR"));
            macros.getDefinitions().addLast(createEmptyMacro("VKAPI_ATTR"));
            macros.getDefinitions().addLast(createEmptyMacro("VKAPI_CALL"));
            CParser parser = new CParser(macros);
            CFile cFile = parser.parse(file);
            return converter.convert(cFile, macros);
        } catch (TraceableException e) {
            Position position = PositionService.getInstance().find(file.getContent(), e.getPosition());
            throw new TraceableException(
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
}