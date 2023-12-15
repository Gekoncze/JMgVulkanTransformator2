package cz.mg.vulkantransformator.services.translator.vk.macos;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.file.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.vk.VkFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.component.VkComponentFileGenerator;

public @Service class VkMacosFileGenerator implements VkFileGenerator {
    private static @Optional VkMacosFileGenerator instance;

    public static @Mandatory VkMacosFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkMacosFileGenerator();
            instance.configuration = VkMacosConfiguration.getInstance();
            instance.vkLibraryCodeGenerator = VkComponentFileGenerator.getInstance();
        }
        return instance;
    }

    private VkMacosConfiguration configuration;
    private VkComponentFileGenerator vkLibraryCodeGenerator;

    private VkMacosFileGenerator() {
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return configuration.getSourceFileName();
    }

    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        return vkLibraryCodeGenerator.generateFiles(root, configuration);
    }
}
