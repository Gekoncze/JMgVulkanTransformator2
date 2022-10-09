package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;

public class VkLibraryGenerator {
    private static @Optional VkLibraryGenerator instance;

    public static @Mandatory VkLibraryGenerator getInstance() {
        if (instance == null) {
            instance = new VkLibraryGenerator();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private CodeGenerator codeGenerator;

    private VkLibraryGenerator() {
    }

    public @Mandatory String getName() {
        return "VkLibrary";
    }

    public @Mandatory List<String> generateJava(@Mandatory VkLibraryConfiguration configuration) {
        return codeGenerator.generateJavaLibraryClass(configuration, getName());
    }
}
