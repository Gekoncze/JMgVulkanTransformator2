package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Utility class XcbLibraryConfiguration implements LibraryConfiguration {
    private static @Optional XcbLibraryConfiguration instance;

    public static @Mandatory XcbLibraryConfiguration getInstance() {
        if (instance == null) {
            instance = new XcbLibraryConfiguration();
        }
        return instance;
    }

    private XcbLibraryConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "XCB";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgvulkanxcb";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan.xcb";
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
