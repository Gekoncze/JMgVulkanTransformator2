package cz.mg.vulkantransformator.services.translator.vk.ios;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.file.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.vk.VkFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.component.VkComponentFileGenerator;

public @Service class VkIosFileGenerator implements VkFileGenerator {
    private static @Optional VkIosFileGenerator instance;

    public static @Mandatory VkIosFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkIosFileGenerator();
            instance.configuration = VkIosConfiguration.getInstance();
            instance.vkLibraryCodeGenerator = VkComponentFileGenerator.getInstance();
        }
        return instance;
    }

    private VkIosConfiguration configuration;
    private VkComponentFileGenerator vkLibraryCodeGenerator;

    private VkIosFileGenerator() {
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return configuration.getSourceFileName();
    }

    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        return vkLibraryCodeGenerator.generateFiles(root, configuration);
    }
}
