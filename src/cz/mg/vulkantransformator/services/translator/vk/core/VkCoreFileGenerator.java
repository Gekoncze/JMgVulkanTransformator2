package cz.mg.vulkantransformator.services.translator.vk.core;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.vk.component.VkComponentFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkFileGenerator;

public @Service class VkCoreFileGenerator implements VkFileGenerator {
    private static @Optional VkCoreFileGenerator instance;

    public static @Mandatory VkCoreFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkCoreFileGenerator();
            instance.configuration = VkCoreConfiguration.getInstance();
            instance.vkComponentFileGenerator = VkComponentFileGenerator.getInstance();
        }
        return instance;
    }

    private VkCoreConfiguration configuration;
    private VkComponentFileGenerator vkComponentFileGenerator;

    private VkCoreFileGenerator() {
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return configuration.getSourceFileName();
    }

    @Override
    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        return vkComponentFileGenerator.generateFiles(root, configuration);
    }
}
