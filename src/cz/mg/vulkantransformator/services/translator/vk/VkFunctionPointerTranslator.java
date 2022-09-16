package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkFunctionPointer;
import cz.mg.vulkantransformator.services.translator.Index;

public @Service class VkFunctionPointerTranslator implements VkTranslator<VkFunctionPointer> {
    private static @Optional VkFunctionPointerTranslator instance;

    public static @Mandatory VkFunctionPointerTranslator getInstance() {
        if (instance == null) {
            instance = new VkFunctionPointerTranslator();
            instance.componentTranslator = VkComponentTranslator.getInstance();
        }
        return instance;
    }

    private VkComponentTranslator componentTranslator;

    private VkFunctionPointerTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkFunctionPointer.class;
    }

    @Override
    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkFunctionPointer pointer) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            componentTranslator.getCommonJavaHeader(pointer)
        );

        componentTranslator.removeLastEmptyLine(lines);

        lines.addCollectionLast(
            componentTranslator.getCommonJavaFooter(pointer)
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkFunctionPointer pointer) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            componentTranslator.getCommonNativeHeader(pointer)
        );

        lines.addCollectionLast(
            componentTranslator.getCommonNativeFooter(pointer)
        );

        return lines;
    }
}