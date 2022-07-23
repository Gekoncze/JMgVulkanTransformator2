package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkEnum;
import cz.mg.vulkantransformator.entities.vulkan.VkEnumEntry;
import cz.mg.vulkantransformator.services.translator.Index;

public @Service class VkEnumTranslator implements VkTranslator<VkEnum> {
    private static @Optional VkEnumTranslator instance;

    public static @Mandatory VkEnumTranslator getInstance() {
        if (instance == null) {
            instance = new VkEnumTranslator();
            instance.componentTranslator = VkComponentTranslator.getInstance();
            instance.enumEntryTranslator = VkEnumEntryTranslator.getInstance();
        }
        return instance;
    }

    private VkComponentTranslator componentTranslator;
    private VkEnumEntryTranslator enumEntryTranslator;

    private VkEnumTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkEnum.class;
    }

    @Override
    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkEnum enumeration) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            componentTranslator.getCommonJavaHeader(enumeration)
        );

        for (VkEnumEntry entry : enumeration.getEntries()) {
            lines.addCollectionLast(
                enumEntryTranslator.translateJava(enumeration, entry)
            );
        }

        lines.addCollectionLast(
            componentTranslator.getCommonJavaFooter(enumeration)
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkEnum enumeration) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            componentTranslator.getCommonNativeHeader(enumeration)
        );

        for (VkEnumEntry entry : enumeration.getEntries()) {
            lines.addCollectionLast(
                enumEntryTranslator.translateNative(enumeration, entry)
            );
        }

        lines.addCollectionLast(
            componentTranslator.getCommonNativeFooter(enumeration)
        );

        return lines;
    }
}
