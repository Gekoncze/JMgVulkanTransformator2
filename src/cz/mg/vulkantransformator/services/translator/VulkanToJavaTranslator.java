package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;

public @Service class VulkanToJavaTranslator {
    private static VulkanToJavaTranslator instance;

    public static @Mandatory VulkanToJavaTranslator getInstance() {
        if (instance == null) {
            instance = new VulkanToJavaTranslator();
        }
        return instance;
    }

    private VulkanToJavaTranslator() {
    }

    public @Mandatory List<String> export(@Mandatory VkRoot root) {
        throw new UnsupportedOperationException(); // TODO
    }
}
