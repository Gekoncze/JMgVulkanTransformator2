package cz.mg.vulkantransformator.services.translator.vk.wayland;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Utility class VkWaylandConfiguration implements LibraryConfiguration {
    private static @Optional VkWaylandConfiguration instance;

    public static @Mandatory VkWaylandConfiguration getInstance() {
        if (instance == null) {
            instance = new VkWaylandConfiguration();
        }
        return instance;
    }

    private VkWaylandConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "Wayland";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgvulkanwayland";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan.wayland";
    }

    @Override
    public @Mandatory String getSubModulePrefix() {
        return "Wayland";
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
            "#include <wayland-client.h>",
            "#include \"../../c/CMemory.h\""
        );
    }
}
