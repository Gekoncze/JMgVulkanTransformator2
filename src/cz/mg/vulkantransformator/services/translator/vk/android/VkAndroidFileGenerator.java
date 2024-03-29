package cz.mg.vulkantransformator.services.translator.vk.android;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.file.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.EmptyObjectFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.component.VkComponentFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkFileGenerator;

public @Service class VkAndroidFileGenerator implements VkFileGenerator {
    private static @Optional VkAndroidFileGenerator instance;

    public static @Mandatory VkAndroidFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkAndroidFileGenerator();
            instance.configuration = VkAndroidConfiguration.getInstance();
            instance.vkComponentFileGenerator = VkComponentFileGenerator.getInstance();
            instance.emptyObjectFileGenerator = EmptyObjectFileGenerator.getInstance();
        }
        return instance;
    }

    private VkAndroidConfiguration configuration;
    private VkComponentFileGenerator vkComponentFileGenerator;
    private EmptyObjectFileGenerator emptyObjectFileGenerator;

    private VkAndroidFileGenerator() {
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return configuration.getSourceFileName();
    }

    @Override
    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        List<File> files = vkComponentFileGenerator.generateFiles(root, configuration);
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("ANativeWindow", "ANativeWindow", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("AHardwareBuffer", "AHardwareBuffer", configuration));
        return files;
    }
}
