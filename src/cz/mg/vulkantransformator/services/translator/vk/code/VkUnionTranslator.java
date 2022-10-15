package cz.mg.vulkantransformator.services.translator.vk.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkUnion;
import cz.mg.vulkantransformator.entities.vulkan.VkVariable;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.ObjectCodeGenerator;

public @Service class VkUnionTranslator implements VkTranslator<VkUnion> {
    private static @Optional VkUnionTranslator instance;

    public static @Mandatory VkUnionTranslator getInstance() {
        if (instance == null) {
            instance = new VkUnionTranslator();
            instance.fieldTranslator = VkFieldTranslator.getInstance();
            instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private VkFieldTranslator fieldTranslator;
    private ObjectCodeGenerator objectCodeGenerator;
    private CodeGenerator codeGenerator;

    private VkUnionTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkUnion.class;
    }

    @Override
    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkUnion union,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            objectCodeGenerator.getCommonJavaHeader(union.getName(), configuration)
        );

        for (VkVariable field : union.getFields()) {
            lines.addCollectionLast(
                fieldTranslator.translateJava(union, field)
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
        @Mandatory VkUnion union,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeHeader(union.getName(), configuration)
        );

        for (VkVariable field : union.getFields()) {
            lines.addCollectionLast(
                fieldTranslator.translateNative(union, field, configuration)
            );
        }

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeFooter()
        );

        return lines;
    }
}
