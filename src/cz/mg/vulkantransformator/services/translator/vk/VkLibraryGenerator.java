package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.c.CGenerator;

public class VkLibraryGenerator implements CGenerator {
    private static @Optional VkLibraryGenerator instance;

    public static @Mandatory VkLibraryGenerator getInstance() {
        if (instance == null) {
            instance = new VkLibraryGenerator();
        }
        return instance;
    }

    private VkLibraryGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "VkLibrary";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + Configuration.VULKAN_PACKAGE + ";",
            "",
            "public class " + getName() + " {",
            "    public static final String NAME = \"" + Configuration.VULKAN_LIBRARY + "\";",
            "",
            "    public static void load() {",
            "        System.loadLibrary(NAME);",
            "    }",
            "}"
        );
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
