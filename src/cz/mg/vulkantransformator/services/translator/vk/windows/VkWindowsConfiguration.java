package cz.mg.vulkantransformator.services.translator.vk.windows;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameMap;
import cz.mg.vulkantransformator.services.translator.c.CTypenameMap;
import cz.mg.vulkantransformator.services.translator.vk.core.VkCoreTypenameMap;

public @Utility class VkWindowsConfiguration implements LibraryConfiguration {
    private static @Optional VkWindowsConfiguration instance;

    public static @Mandatory VkWindowsConfiguration getInstance() {
        if (instance == null) {
            instance = new VkWindowsConfiguration();
            instance.cTypenameMap = CTypenameMap.getInstance();
            instance.vkTypenameMap = VkCoreTypenameMap.getInstance();
        }
        return instance;
    }

    private CTypenameMap cTypenameMap;
    private VkCoreTypenameMap vkTypenameMap;

    private VkWindowsConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "Windows";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgvulkanwindows";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan.windows";
    }

    @Override
    public @Mandatory String getSubModulePrefix() {
        return "Windows";
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return "vulkan_win32.h";
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
            "VK_USE_PLATFORM_WIN32_KHR",
            "#include <vulkan/vulkan.h>",
            "#include \"../../c/CMemory.h\""
        );
    }

    @Override
    public @Mandatory List<TypenameMap> getTypenameMaps() {
        return new List<>(
            cTypenameMap,
            vkTypenameMap
        );
    }
}
