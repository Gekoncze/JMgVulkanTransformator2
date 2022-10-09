package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Utility class AndroidLibraryConfiguration implements LibraryConfiguration {
    private static @Optional AndroidLibraryConfiguration instance;

    public static @Mandatory AndroidLibraryConfiguration getInstance() {
        if (instance == null) {
            instance = new AndroidLibraryConfiguration();
        }
        return instance;
    }

    private AndroidLibraryConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "Android";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgvulkanandroid";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan.android";
    }

    @Override
    public @Mandatory List<String> getJavaDependencies() {
        return new List<>(
            "import cz.mg.c.*;",
            "import cz.mg.vulkan.*;"
        );
    }

    @Override
    public @Mandatory List<String> getNativeDependencies() {
        return new List<>(
            "#include <vulkan/vulkan.h>",
            "#include \"../../c/CMemory.h\""
        );
    }
}
