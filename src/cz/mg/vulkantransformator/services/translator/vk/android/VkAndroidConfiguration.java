package cz.mg.vulkantransformator.services.translator.vk.android;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameMap;
import cz.mg.vulkantransformator.services.translator.c.CTypenameMap;
import cz.mg.vulkantransformator.services.translator.vk.core.VkCoreTypenameMap;

public @Utility class VkAndroidConfiguration implements LibraryConfiguration {
    private static @Optional VkAndroidConfiguration instance;

    public static @Mandatory VkAndroidConfiguration getInstance() {
        if (instance == null) {
            instance = new VkAndroidConfiguration();
            instance.cTypenameMap = CTypenameMap.getInstance();
            instance.vkCoreTypenameMap = VkCoreTypenameMap.getInstance();
        }
        return instance;
    }

    private CTypenameMap cTypenameMap;
    private VkCoreTypenameMap vkCoreTypenameMap;

    private VkAndroidConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "Android";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgvulkanandroid";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan.android";
    }

    @Override
    public @Mandatory String getSubModulePrefix() {
        return "Android";
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return "vulkan_android.h";
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
            "#define VK_USE_PLATFORM_ANDROID_KHR",
            "#include <vulkan/vulkan.h>",
            "#include <native_window.h>",
            "#include <hardware_buffer_jni.h>",
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
