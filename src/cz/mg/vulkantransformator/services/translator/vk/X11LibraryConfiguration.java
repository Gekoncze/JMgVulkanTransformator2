package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Utility class X11LibraryConfiguration implements LibraryConfiguration {
    private static @Optional X11LibraryConfiguration instance;

    public static @Mandatory X11LibraryConfiguration getInstance() {
        if (instance == null) {
            instance = new X11LibraryConfiguration();
        }
        return instance;
    }

    private X11LibraryConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "X11";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgvulkanx11";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan.x11";
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
            "#include \"../../c/CMemory.h\""
        );
    }
}
