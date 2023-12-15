package cz.mg.vulkantransformator.services.translator.vk.wayland;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.file.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.EmptyObjectFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.component.VkComponentFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkFileGenerator;

public @Service class VkWaylandFileGenerator implements VkFileGenerator {
    private static @Optional VkWaylandFileGenerator instance;

    public static @Mandatory VkWaylandFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkWaylandFileGenerator();
            instance.configuration = VkWaylandConfiguration.getInstance();
            instance.vkComponentFileGenerator = VkComponentFileGenerator.getInstance();
            instance.emptyObjectFileGenerator = EmptyObjectFileGenerator.getInstance();
        }
        return instance;
    }

    private VkWaylandConfiguration configuration;
    private VkComponentFileGenerator vkComponentFileGenerator;
    private EmptyObjectFileGenerator emptyObjectFileGenerator;

    private VkWaylandFileGenerator() {
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return configuration.getSourceFileName();
    }

    @Override
    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        List<File> files = vkComponentFileGenerator.generateFiles(root, configuration);
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("WlDisplay", "wl_display", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("WlSurface", "wl_surface", configuration));
        return files;
    }
}
