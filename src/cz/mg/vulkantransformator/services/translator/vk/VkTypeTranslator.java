package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkType;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.vk.types.VkBool32TypeTranslator;
import cz.mg.vulkantransformator.services.translator.vk.types.VkSpecialTypeTranslator;

public @Service class VkTypeTranslator implements VkTranslator<VkType> {
    private static @Optional VkTypeTranslator instance;

    public static @Mandatory VkTypeTranslator getInstance() {
        if (instance == null) {
            instance = new VkTypeTranslator();
            instance.vkComponentTranslator = VkComponentTranslator.getInstance();
            instance.vkBool32TypeTranslator = VkBool32TypeTranslator.getInstance();
        }
        return instance;
    }

    private VkComponentTranslator vkComponentTranslator;
    private VkBool32TypeTranslator vkBool32TypeTranslator;

    private VkTypeTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkType.class;
    }

    @Override
    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkType type) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            vkComponentTranslator.getCommonJavaHeader(type)
        );

        List<VkSpecialTypeTranslator> specialTypeTranslators = new List<>(
            vkBool32TypeTranslator
        );

        for (VkSpecialTypeTranslator specialTypeTranslator : specialTypeTranslators) {
            if (specialTypeTranslator.getName().equals(type.getName())) {
                lines.addCollectionLast(
                    specialTypeTranslator.translateJava(index, type)
                );
            }
        }

        lines.addCollectionLast(
            vkComponentTranslator.getCommonJavaFooter(type)
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkType type) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            vkComponentTranslator.getCommonNativeHeader(type)
        );

        List<VkSpecialTypeTranslator> specialTypeTranslators = new List<>(
            vkBool32TypeTranslator
        );

        for (VkSpecialTypeTranslator specialTypeTranslator : specialTypeTranslators) {
            if (specialTypeTranslator.getName().equals(type.getName())) {
                lines.addCollectionLast(
                    specialTypeTranslator.translateNative(index, type)
                );
            }
        }

        lines.addCollectionLast(
            vkComponentTranslator.getCommonNativeFooter(type)
        );

        return lines;
    }
}
