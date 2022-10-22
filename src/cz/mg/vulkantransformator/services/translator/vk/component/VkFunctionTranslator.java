package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkVariable;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.ObjectCodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.Index;

public @Service class VkFunctionTranslator implements VkTranslator<VkFunction> {
    private static @Optional VkFunctionTranslator instance;

    public static @Mandatory VkFunctionTranslator getInstance() {
        if (instance == null) {
            instance = new VkFunctionTranslator();
            instance.fieldTranslator = VkFieldTranslator.getInstance();
            instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private VkFieldTranslator fieldTranslator;
    private ObjectCodeGenerator objectCodeGenerator;
    private CodeGenerator codeGenerator;

    private VkFunctionTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkFunction.class;
    }

    @Override
    public @Mandatory String getJavaName(@Mandatory VkFunction function) {
        return function.getName().replaceFirst("vk", "Vkf");
    }

    @Override
    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkFunction function,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            objectCodeGenerator.getCommonJavaHeader(getJavaName(function), configuration)
        );

        for (VkVariable field : function.getInput()) {
            lines.addCollectionLast(
                fieldTranslator.translateJava(getJavaName(function), field, configuration)
            );
        }

        if (!isVoid(function.getOutput())) {
            lines.addCollectionLast(
                fieldTranslator.translateJava(
                    getJavaName(function),
                    convertOutput(function.getOutput()),
                    configuration
                )
            );
        }

        codeGenerator.removeLastEmptyLine(lines);

        lines.addCollectionLast(
            objectCodeGenerator.getCommonJavaFooter()
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory VkFunction function,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeHeader(getJavaName(function), getJavaName(function), configuration)
        );

        for (VkVariable field : function.getInput()) {
            lines.addCollectionLast(
                fieldTranslator.translateNative(getJavaName(function), field, configuration)
            );
        }

        if (!isVoid(function.getOutput())) {
            lines.addCollectionLast(
                fieldTranslator.translateNative(getJavaName(function), convertOutput(function.getOutput()), configuration)
            );
        }

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeFooter()
        );

        return lines;
    }

    private boolean isVoid(@Mandatory VkVariable variable) {
        return variable.getTypename().equals("void");
    }

    private @Mandatory VkVariable convertOutput(@Mandatory VkVariable output) {
        return new VkVariable(
            output.getTypename(),
            output.getPointers(),
            "result",
            output.getArray()
        );
    }
}
