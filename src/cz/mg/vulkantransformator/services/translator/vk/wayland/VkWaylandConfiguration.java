package cz.mg.vulkantransformator.services.translator.vk.wayland;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameMap;
import cz.mg.vulkantransformator.services.translator.c.CTypenameMap;
import cz.mg.vulkantransformator.services.translator.vk.core.VkCoreTypenameMap;

public @Utility class VkWaylandConfiguration implements LibraryConfiguration {
    private static @Optional VkWaylandConfiguration instance;

    public static @Mandatory VkWaylandConfiguration getInstance() {
        if (instance == null) {
            instance = new VkWaylandConfiguration();
            instance.cTypenameMap = CTypenameMap.getInstance();
            instance.vkCoreTypenameMap = VkCoreTypenameMap.getInstance();
            instance.waylandTypenameMap = WaylandTypenameMap.getInstance();
        }
        return instance;
    }

    private CTypenameMap cTypenameMap;
    private VkCoreTypenameMap vkCoreTypenameMap;
    private WaylandTypenameMap waylandTypenameMap;

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
    public @Mandatory String getSourceFileName() {
        return "vulkan_wayland.h";
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
            "#define VK_USE_PLATFORM_WAYLAND_KHR",
            "#include <vulkan/vulkan.h>",
            "#include <wayland-client.h>",
            "#include \"../../c/CMemory.h\""
        );
    }

    @Override
    public @Mandatory List<String> getLibraryDependencies() {
        return new List<>(
            "jmgc", "vulkan"
        );
    }

    @Override
    public @Mandatory List<TypenameMap> getTypenameMaps() {
        return new List<>(
            cTypenameMap,
            vkCoreTypenameMap,
            waylandTypenameMap
        );
    }
}
