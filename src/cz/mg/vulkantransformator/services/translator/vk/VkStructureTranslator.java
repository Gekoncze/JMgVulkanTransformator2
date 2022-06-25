package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkField;
import cz.mg.vulkantransformator.entities.vulkan.VkStructure;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.index.Index;

public @Service class VkStructureTranslator implements VkTranslator<VkStructure> {
    private static @Optional VkStructureTranslator instance;

    public static @Mandatory VkStructureTranslator getInstance() {
        if (instance == null) {
            instance = new VkStructureTranslator();
            instance.common = Common.getInstance();
            instance.fieldTranslator = VkFieldTranslator.getInstance();
        }
        return instance;
    }

    private Common common;
    private VkFieldTranslator fieldTranslator;

    private VkStructureTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkStructure.class;
    }

    @Override
    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkStructure structure) {
        List<String> lines = new List<>();

        lines.addLast("package " + Configuration.PACKAGE + ";");
        lines.addLast("");
        lines.addLast("public class " + structure.getName() + " {");
        lines.addLast("    private final long address;");
        lines.addLast("");
        lines.addLast("    public " + structure.getName() + "(long address) {");
        lines.addLast("        this.address = address;");
        lines.addLast("    }");
        lines.addLast("");
        lines.addLast("    private static native long sizeof();");
        lines.addLast("");

        for (VkField field : structure.getFields()) {
            lines.addCollectionLast(
                fieldTranslator.translateJava(field)
            );

            if (field != structure.getFields().getLast()) {
                lines.addLast("");
            }
        }

        lines.addLast("}");

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkStructure structure) {
        List<String> lines = new List<>();

        lines.addCollectionLast(common.getCommonHeader(structure));

//        lines.addLast(todo);

        return lines;
    }
}
