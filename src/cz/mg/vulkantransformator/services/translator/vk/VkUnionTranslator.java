package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkUnion;
import cz.mg.vulkantransformator.entities.vulkan.VkVariable;
import cz.mg.vulkantransformator.services.translator.Index;

public @Service class VkUnionTranslator implements VkTranslator<VkUnion> {
    private static @Optional VkUnionTranslator instance;

    public static @Mandatory VkUnionTranslator getInstance() {
        if (instance == null) {
            instance = new VkUnionTranslator();
            instance.vkComponentTranslator = VkComponentTranslator.getInstance();
            instance.fieldTranslator = VkFieldTranslator.getInstance();
        }
        return instance;
    }

    private VkComponentTranslator vkComponentTranslator;
    private VkFieldTranslator fieldTranslator;

    private VkUnionTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkUnion.class;
    }

    @Override
    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkUnion union) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            vkComponentTranslator.getCommonJavaHeader(union)
        );

        for (VkVariable field : union.getFields()) {
            lines.addCollectionLast(
                fieldTranslator.translateJava(union, field)
            );

            if (field != union.getFields().getLast()) {
                lines.addLast("");
            }
        }

        lines.addCollectionLast(
            vkComponentTranslator.getCommonJavaFooter(union)
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkUnion union) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            vkComponentTranslator.getCommonNativeHeader(union)
        );

        for (VkVariable field : union.getFields()) {
            lines.addCollectionLast(
                fieldTranslator.translateNative(union, field)
            );
        }

        lines.addCollectionLast(
            vkComponentTranslator.getCommonNativeFooter(union)
        );

        return lines;
    }
}
