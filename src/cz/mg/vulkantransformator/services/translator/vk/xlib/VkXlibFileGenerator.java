package cz.mg.vulkantransformator.services.translator.vk.xlib;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.EmptyObjectFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.component.VkComponentFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkFileGenerator;

public @Service class VkXlibFileGenerator implements VkFileGenerator {
    private static @Optional VkXlibFileGenerator instance;

    public static @Mandatory VkXlibFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkXlibFileGenerator();
            instance.configuration = VkXlibConfiguration.getInstance();
            instance.vkComponentFileGenerator = VkComponentFileGenerator.getInstance();
            instance.emptyObjectFileGenerator = EmptyObjectFileGenerator.getInstance();
        }
        return instance;
    }

    private VkXlibConfiguration configuration;
    private VkComponentFileGenerator vkComponentFileGenerator;
    private EmptyObjectFileGenerator emptyObjectFileGenerator;

    private VkXlibFileGenerator() {
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return configuration.getSourceFileName();
    }

    @Override
    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        List<File> files = vkComponentFileGenerator.generateFiles(root, configuration);
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("Display", "Display", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("Window", "Window", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("VisualID", "VisualID", configuration));
        return files;
    }
}