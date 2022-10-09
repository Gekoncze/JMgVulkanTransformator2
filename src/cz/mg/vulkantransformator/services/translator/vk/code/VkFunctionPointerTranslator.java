package cz.mg.vulkantransformator.services.translator.vk.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkFunctionPointer;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Service class VkFunctionPointerTranslator implements VkTranslator<VkFunctionPointer> {
    private static @Optional VkFunctionPointerTranslator instance;

    public static @Mandatory VkFunctionPointerTranslator getInstance() {
        if (instance == null) {
            instance = new VkFunctionPointerTranslator();
            instance.componentTranslator = VkComponentTranslator.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private VkComponentTranslator componentTranslator;
    private CodeGenerator codeGenerator;

    private VkFunctionPointerTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkFunctionPointer.class;
    }

    @Override
    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkFunctionPointer pointer,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            componentTranslator.getCommonJavaHeader(pointer, configuration)
        );

        codeGenerator.removeLastEmptyLine(lines);

        lines.addCollectionLast(
            componentTranslator.getCommonJavaFooter(pointer)
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory VkFunctionPointer pointer,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            componentTranslator.getCommonNativeHeader(pointer, configuration)
        );

        lines.addCollectionLast(
            componentTranslator.getCommonNativeFooter(pointer)
        );

        return lines;
    }
}
