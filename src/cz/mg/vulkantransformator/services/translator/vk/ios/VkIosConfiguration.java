package cz.mg.vulkantransformator.services.translator.vk.ios;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameMap;
import cz.mg.vulkantransformator.services.translator.c.CTypenameMap;
import cz.mg.vulkantransformator.services.translator.vk.core.VkCoreTypenameMap;

public @Utility class VkIosConfiguration implements LibraryConfiguration {
    private static @Optional VkIosConfiguration instance;

    public static @Mandatory VkIosConfiguration getInstance() {
        if (instance == null) {
            instance = new VkIosConfiguration();
            instance.cTypenameMap = CTypenameMap.getInstance();
            instance.vkTypenameMap = VkCoreTypenameMap.getInstance();
        }
        return instance;
    }

    private CTypenameMap cTypenameMap;
    private VkCoreTypenameMap vkTypenameMap;

    private VkIosConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "iOS";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgvulkanios";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan.ios";
    }

    @Override
    public @Mandatory String getSubModulePrefix() {
        return "Ios";
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return "vulkan_ios.h";
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
            "#define VK_USE_PLATFORM_IOS_MVK",
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
