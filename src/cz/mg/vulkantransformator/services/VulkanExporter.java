package cz.mg.vulkantransformator.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;

public @Service class VulkanExporter {
    private static VulkanExporter instance;

    public static @Mandatory VulkanExporter getInstance() {
        if (instance == null) {
            instance = new VulkanExporter();
        }
        return instance;
    }

    private VulkanExporter() {
    }

    public @Mandatory List<String> export(@Mandatory VkRoot root) {
        throw new UnsupportedOperationException(); // TODO
    }
}
