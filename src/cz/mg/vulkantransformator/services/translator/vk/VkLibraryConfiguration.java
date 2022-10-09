package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Utility class VkLibraryConfiguration implements LibraryConfiguration {
    private static @Optional VkLibraryConfiguration instance;

    public static @Mandatory VkLibraryConfiguration getInstance() {
        if (instance == null) {
            instance = new VkLibraryConfiguration();
        }
        return instance;
    }

    private VkLibraryConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "Vulkan";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgvulkan";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan";
    }

    @Override
    public @Mandatory List<String> getJavaDependencies() {
        return new List<>(
            "import cz.mg.c.*;"
        );
    }

    @Override
    public @Mandatory List<String> getNativeDependencies() {
        return new List<>(
            "#include <vulkan/vulkan.h>",
            "#include \"../c/CMemory.h\""
        );
    }
}
