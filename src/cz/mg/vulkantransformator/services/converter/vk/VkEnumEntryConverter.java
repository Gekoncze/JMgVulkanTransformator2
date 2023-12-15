package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CEnumEntry;
import cz.mg.tokenizer.entities.Token;
import cz.mg.vulkantransformator.entities.vulkan.VkEnumEntry;

public @Service class VkEnumEntryConverter {
    private static volatile @Service VkEnumEntryConverter instance;

    public static @Service VkEnumEntryConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkEnumEntryConverter();
                }
            }
        }
        return instance;
    }

    private VkEnumEntryConverter() {
    }

    public @Mandatory VkEnumEntry convert(@Mandatory CEnumEntry enumEntry) {
        VkEnumEntry entry = new VkEnumEntry();
        entry.setName(enumEntry.getName().getText());
        if (enumEntry.getExpression() != null) {
            for (Token token : enumEntry.getExpression()) {
                entry.getExpression().addLast(token.getText());
            }
        }
        return entry;
    }
}
