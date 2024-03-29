package cz.mg.vulkantransformator.services.translator.vk.core;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameMap;
import cz.mg.vulkantransformator.services.translator.c.CTypenameMap;

public @Component class VkCoreConfiguration implements LibraryConfiguration {
    private static @Optional VkCoreConfiguration instance;

    public static @Mandatory VkCoreConfiguration getInstance() {
        if (instance == null) {
            instance = new VkCoreConfiguration();
            instance.cTypenameMap = CTypenameMap.getInstance();
            instance.vkCoreTypenameMap = VkCoreTypenameMap.getInstance();
        }
        return instance;
    }

    private CTypenameMap cTypenameMap;
    private VkCoreTypenameMap vkCoreTypenameMap;

    private VkCoreConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "Vulkan";
    }

    @Override
    public @Mandatory String getJavaLibraryName() {
        return "JMgVulkanCore";
    }

    @Override
    public @Mandatory String getNativeLibraryName() {
        return "jmgvulkancore";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan";
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return "vulkan_core.h";
    }

    @Override
    public @Mandatory List<String> getJavaDependencies() {
        return new List<>(
            "import cz.mg.c.*;"
        );
    }

    @Override
    public @Mandatory List<String> getJavaLibraryDependencies() {
        return new List<>(
            "../c/JMgC.jar"
        );
    }

    @Override
    public @Mandatory List<String> getNativeDependencies() {
        return new List<>(
            "#include <vulkan/vulkan.h>",
            "#include \"../c/CMemory.h\""
        );
    }

    @Override
    public @Mandatory List<String> getNativeLibraryDependencies() {
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
