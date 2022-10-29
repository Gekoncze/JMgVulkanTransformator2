package cz.mg.vulkantransformator.services.translator.vk.xlib;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameMap;
import cz.mg.vulkantransformator.services.translator.c.CTypenameMap;
import cz.mg.vulkantransformator.services.translator.vk.core.VkCoreTypenameMap;

public @Utility class VkXlibConfiguration implements LibraryConfiguration {
    private static @Optional VkXlibConfiguration instance;

    public static @Mandatory VkXlibConfiguration getInstance() {
        if (instance == null) {
            instance = new VkXlibConfiguration();
            instance.cTypenameMap = CTypenameMap.getInstance();
            instance.vkCoreTypenameMap = VkCoreTypenameMap.getInstance();
        }
        return instance;
    }

    private CTypenameMap cTypenameMap;
    private VkCoreTypenameMap vkCoreTypenameMap;

    private VkXlibConfiguration() {
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
    public @Mandatory String getSubModulePrefix() {
        return "Xlib";
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return "vulkan_xlib.h";
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
            "#define VK_USE_PLATFORM_XLIB_KHR",
            "#include <vulkan/vulkan.h>",
            "#include <X11/Xlib.h>",
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
            vkCoreTypenameMap
        );
    }
}
