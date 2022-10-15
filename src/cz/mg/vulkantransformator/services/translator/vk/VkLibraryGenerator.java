package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

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

    public @Mandatory String getName(@Mandatory LibraryConfiguration configuration) {
        return "Vk" + configuration.getSubModulePrefix() + "Library";
    }

    public @Mandatory List<String> generateJava(@Mandatory LibraryConfiguration configuration) {
        return codeGenerator.generateJavaLibraryClass(configuration, getName(configuration));
    }
}
