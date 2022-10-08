package cz.mg.vulkantransformator.services.translator.vk.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkType;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.vk.code.types.VkBool32TypeTranslator;
import cz.mg.vulkantransformator.services.translator.vk.code.types.VkDeviceSizeTypeTranslator;
import cz.mg.vulkantransformator.services.translator.vk.code.types.VkSpecialTypeTranslator;

public @Service class VkTypeTranslator implements VkTranslator<VkType> {
    private static @Optional VkTypeTranslator instance;

    public static @Mandatory VkTypeTranslator getInstance() {
        if (instance == null) {
            instance = new VkTypeTranslator();
            instance.componentTranslator = VkComponentTranslator.getInstance();
            instance.specialTypeTranslators = new List<>(
                VkBool32TypeTranslator.getInstance(),
                VkDeviceSizeTypeTranslator.getInstance()
            );
        }
        return instance;
    }

    private VkComponentTranslator componentTranslator;
    private List<VkSpecialTypeTranslator> specialTypeTranslators;

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
            componentTranslator.getCommonJavaHeader(type)
        );

        for (VkSpecialTypeTranslator specialTypeTranslator : specialTypeTranslators) {
            if (specialTypeTranslator.getName().equals(type.getName())) {
                lines.addCollectionLast(
                    specialTypeTranslator.translateJava(index, type)
                );
            }
        }

        lines.addCollectionLast(
            componentTranslator.getCommonJavaFooter(type)
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkType type) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            componentTranslator.getCommonNativeHeader(type)
        );

        for (VkSpecialTypeTranslator specialTypeTranslator : specialTypeTranslators) {
            if (specialTypeTranslator.getName().equals(type.getName())) {
                lines.addCollectionLast(
                    specialTypeTranslator.translateNative(index, type)
                );
            }
        }

        lines.addCollectionLast(
            componentTranslator.getCommonNativeFooter(type)
        );

        return lines;
    }
}
