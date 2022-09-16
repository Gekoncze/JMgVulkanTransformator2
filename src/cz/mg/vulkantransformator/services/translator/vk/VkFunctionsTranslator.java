package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.c.CMemoryGenerator;

public @Service class VkFunctionsTranslator {
    private static @Optional VkFunctionsTranslator instance;

    public static @Mandatory VkFunctionsTranslator getInstance() {
        if (instance == null) {
            instance = new VkFunctionsTranslator();
            instance.functionTranslator = VkFunctionTranslator.getInstance();
            instance.componentTranslator = VkComponentTranslator.getInstance();
            instance.memoryGenerator = CMemoryGenerator.getInstance();
        }
        return instance;
    }

    private VkFunctionTranslator functionTranslator;
    private VkComponentTranslator componentTranslator;
    private CMemoryGenerator memoryGenerator;

    private VkFunctionsTranslator() {
    }

    public @Mandatory String getName() {
        return "VkInterface";
    }

    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkRoot root) {
        List<String> lines = new List<>();

        lines.addCollectionLast(new List<>(
            "package " + Configuration.VULKAN_PACKAGE + ";",
            "",
            "import " + Configuration.C_PACKAGE + ".*;",
            "",
            "public class " + getName() + " {",
            "    public " + getName() + "() {",
            "    }",
            ""
        ));

        for (VkComponent component : root.getComponents()) {
            if (component instanceof VkFunction) {
                VkFunction function = (VkFunction) component;
                lines.addCollectionLast(functionTranslator.translateJava(index, function));
                lines.addLast("");
            }
        }

        componentTranslator.removeLastEmptyLine(lines);

        lines.addCollectionLast(new List<>(
            "}"
        ));

        return lines;
    }

    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkRoot root) {
        List<String> lines = new List<>();

        lines.addCollectionLast(new List<>(
            "#include <vulkan/vulkan.h>",
            "#include \"" + memoryGenerator.getName() + ".h\"",
            ""
        ));

        for (VkComponent component : root.getComponents()) {
            if (component instanceof VkFunction) {
                VkFunction function = (VkFunction) component;
                lines.addCollectionLast(functionTranslator.translateNative(index, function));
                lines.addLast("");
            }
        }

        return lines;
    }
}
