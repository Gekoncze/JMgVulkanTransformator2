package cz.mg.vulkantransformator.services.translator.vk.xcb;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameMap;
import cz.mg.vulkantransformator.services.translator.c.CTypenameMap;
import cz.mg.vulkantransformator.services.translator.vk.VkTypenameMap;

public @Utility class VkXcbConfiguration implements LibraryConfiguration {
    private static @Optional VkXcbConfiguration instance;

    public static @Mandatory VkXcbConfiguration getInstance() {
        if (instance == null) {
            instance = new VkXcbConfiguration();
            instance.cTypenameMap = CTypenameMap.getInstance();
            instance.vkTypenameMap = VkTypenameMap.getInstance();
            instance.xcbTypenameMap = XcbTypenameMap.getInstance();
        }
        return instance;
    }

    private CTypenameMap cTypenameMap;
    private VkTypenameMap vkTypenameMap;
    private XcbTypenameMap xcbTypenameMap;

    private VkXcbConfiguration() {
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
    public @Mandatory String getSubModulePrefix() {
        return "Xcb";
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

    @Override
    public @Mandatory List<TypenameMap> getTypenameMaps() {
        return new List<>(
            cTypenameMap,
            vkTypenameMap,
            xcbTypenameMap
        );
    }
}
