package cz.mg.vulkantransformator.services.translator.vk.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkType;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.ObjectCodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.code.types.VkBool32TypeTranslator;
import cz.mg.vulkantransformator.services.translator.vk.code.types.VkDeviceSizeTypeTranslator;
import cz.mg.vulkantransformator.services.translator.vk.code.types.VkSpecialTypeTranslator;

public @Service class VkTypeTranslator implements VkTranslator<VkType> {
    private static @Optional VkTypeTranslator instance;

    public static @Mandatory VkTypeTranslator getInstance() {
        if (instance == null) {
            instance = new VkTypeTranslator();
            instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
            instance.specialTypeTranslators = new List<>(
                VkBool32TypeTranslator.getInstance(),
                VkDeviceSizeTypeTranslator.getInstance()
            );
        }
        return instance;
    }

    private ObjectCodeGenerator objectCodeGenerator;
    private List<VkSpecialTypeTranslator> specialTypeTranslators;

    private VkTypeTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkType.class;
    }

    @Override
    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkType type,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            objectCodeGenerator.getCommonJavaHeader(type.getName(), configuration)
        );

        for (VkSpecialTypeTranslator specialTypeTranslator : specialTypeTranslators) {
            if (specialTypeTranslator.getName().equals(type.getName())) {
                lines.addCollectionLast(
                    specialTypeTranslator.translateJava(index, type, configuration)
                );
            }
        }

        lines.addCollectionLast(
            objectCodeGenerator.getCommonJavaFooter()
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory VkType type,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeHeader(type.getName(), type.getName(), configuration)
        );

        for (VkSpecialTypeTranslator specialTypeTranslator : specialTypeTranslators) {
            if (specialTypeTranslator.getName().equals(type.getName())) {
                lines.addCollectionLast(
                    specialTypeTranslator.translateNative(index, type, configuration)
                );
            }
        }

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeFooter()
        );

        return lines;
    }
}
