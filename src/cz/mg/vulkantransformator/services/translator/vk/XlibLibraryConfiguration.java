package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Utility class XlibLibraryConfiguration implements LibraryConfiguration {
    private static @Optional XlibLibraryConfiguration instance;

    public static @Mandatory XlibLibraryConfiguration getInstance() {
        if (instance == null) {
            instance = new XlibLibraryConfiguration();
        }
        return instance;
    }

    private XlibLibraryConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "Xlib";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgvulkanxlib";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan.xlib";
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
