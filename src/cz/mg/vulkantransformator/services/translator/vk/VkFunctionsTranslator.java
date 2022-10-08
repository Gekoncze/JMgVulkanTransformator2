package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.Index;

public @Service class VkFunctionsTranslator {
    private static @Optional VkFunctionsTranslator instance;

    public static @Mandatory VkFunctionsTranslator getInstance() {
        if (instance == null) {
            instance = new VkFunctionsTranslator();
            instance.functionTranslator = VkFunctionTranslator.getInstance();
            instance.configuration = VkLibraryConfiguration.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private VkFunctionTranslator functionTranslator;
    private VkLibraryConfiguration configuration;
    private CodeGenerator codeGenerator;

    private VkFunctionsTranslator() {
    }

    public @Mandatory String getName() {
        return "VkInterface";
    }

    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkRoot root) {
        List<String> lines = codeGenerator.generateJavaHeader(configuration);

        lines.addLast("public class " + getName() + " {");
        lines.addLast("    public " + getName() + "() {");
        lines.addLast("    }");
        lines.addLast("");

        for (VkComponent component : root.getComponents()) {
            if (component instanceof VkFunction) {
                VkFunction function = (VkFunction) component;
                lines.addCollectionLast(functionTranslator.translateJava(index, function));
                lines.addLast("");
            }
        }

        codeGenerator.removeLastEmptyLine(lines);

        lines.addLast("}");

        return lines;
    }

    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkRoot root) {
        List<String> lines = codeGenerator.generateNativeHeader(configuration);

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
