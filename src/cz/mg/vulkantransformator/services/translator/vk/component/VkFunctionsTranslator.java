package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Service class VkFunctionsTranslator {
    private static @Optional VkFunctionsTranslator instance;

    public static @Mandatory VkFunctionsTranslator getInstance() {
        if (instance == null) {
            instance = new VkFunctionsTranslator();
            instance.functionTranslator = VkFunctionTranslator.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private VkFunctionTranslator functionTranslator;
    private CodeGenerator codeGenerator;

    private VkFunctionsTranslator() {
    }

    public @Mandatory String getName(@Mandatory LibraryConfiguration configuration) {
        return "Vk" + configuration.getSubModulePrefix() + "Interface";
    }

    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkRoot root,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = codeGenerator.generateJavaHeader(configuration);

        lines.addLast("public class " + getName(configuration) + " {");
        lines.addLast("    public " + getName(configuration) + "() {");
        lines.addLast("    }");
        lines.addLast("");

        for (VkComponent component : root.getComponents()) {
            if (component instanceof VkFunction) {
                VkFunction function = (VkFunction) component;
                lines.addCollectionLast(functionTranslator.translateJava(index, function, configuration));
                lines.addLast("");
            }
        }

        codeGenerator.removeLastEmptyLine(lines);

        lines.addLast("}");

        return lines;
    }

    public @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory VkRoot root,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = codeGenerator.generateNativeHeader(configuration);

        for (VkComponent component : root.getComponents()) {
            if (component instanceof VkFunction) {
                VkFunction function = (VkFunction) component;
                lines.addCollectionLast(functionTranslator.translateNative(index, function, configuration));
                lines.addLast("");
            }
        }

        return lines;
    }
}
