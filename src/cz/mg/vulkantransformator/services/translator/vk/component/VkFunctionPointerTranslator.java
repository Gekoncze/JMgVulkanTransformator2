package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkFunctionPointer;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.ObjectCodeGenerator;

public @Service class VkFunctionPointerTranslator implements VkTranslator<VkFunctionPointer> {
    private static @Optional VkFunctionPointerTranslator instance;

    public static @Mandatory VkFunctionPointerTranslator getInstance() {
        if (instance == null) {
            instance = new VkFunctionPointerTranslator();
            instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private ObjectCodeGenerator objectCodeGenerator;
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
            objectCodeGenerator.getCommonJavaHeading(pointer.getName(), configuration)
        );

        codeGenerator.removeLastEmptyLine(lines);

        lines.addCollectionLast(
            objectCodeGenerator.getCommonJavaFooter()
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
            objectCodeGenerator.getCommonNativeHeading(pointer.getName(), pointer.getName(), null, configuration)
        );

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeFooter()
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNativeHeader(
        @Mandatory Index index,
        @Mandatory VkFunctionPointer component,
        @Mandatory LibraryConfiguration configuration
    ) {
        return new List<>();
    }
}
