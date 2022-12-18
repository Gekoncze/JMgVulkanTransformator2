package cz.mg.vulkantransformator.services.translator.vk.macos;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameMap;
import cz.mg.vulkantransformator.services.translator.c.CTypenameMap;
import cz.mg.vulkantransformator.services.translator.vk.core.VkCoreTypenameMap;

public @Utility class VkMacosConfiguration implements LibraryConfiguration {
    private static @Optional VkMacosConfiguration instance;

    public static @Mandatory VkMacosConfiguration getInstance() {
        if (instance == null) {
            instance = new VkMacosConfiguration();
            instance.cTypenameMap = CTypenameMap.getInstance();
            instance.vkTypenameMap = VkCoreTypenameMap.getInstance();
        }
        return instance;
    }

    private CTypenameMap cTypenameMap;
    private VkCoreTypenameMap vkTypenameMap;

    private VkMacosConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "macOS";
    }

    @Override
    public @Mandatory String getJavaLibraryName() {
        return "JMgVulkanMacos";
    }

    @Override
    public @Mandatory String getNativeLibraryName() {
        return "jmgvulkanmacos";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan.macos";
    }

    @Override
    public @Mandatory String getSubModulePrefix() {
        return "Macos";
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return "vulkan_macos.h";
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
            "#define VK_USE_PLATFORM_MACOS_MVK",
            "#include <vulkan/vulkan.h>",
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
            vkTypenameMap
        );
    }
}
