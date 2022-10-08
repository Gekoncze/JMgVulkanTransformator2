package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.c.CGenerator;

public class VkLibraryGenerator implements CGenerator {
    private static @Optional VkLibraryGenerator instance;

    public static @Mandatory VkLibraryGenerator getInstance() {
        if (instance == null) {
            instance = new VkLibraryGenerator();
            instance.configuration = VkLibraryConfiguration.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private VkLibraryConfiguration configuration;
    private CodeGenerator codeGenerator;

    private VkLibraryGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "VkLibrary";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return codeGenerator.generateJavaLibraryClass(configuration, getName());
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return new List<>();
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
