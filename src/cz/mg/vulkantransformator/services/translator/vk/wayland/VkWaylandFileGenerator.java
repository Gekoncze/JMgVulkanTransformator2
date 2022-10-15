package cz.mg.vulkantransformator.services.translator.vk.wayland;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.EmptyObjectFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkLibraryCodeGenerator;

public @Service class VkWaylandFileGenerator {
    private static @Optional VkWaylandFileGenerator instance;

    public static @Mandatory VkWaylandFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkWaylandFileGenerator();
            instance.configuration = VkWaylandConfiguration.getInstance();
            instance.vkLibraryCodeGenerator = VkLibraryCodeGenerator.getInstance();
            instance.emptyObjectFileGenerator = EmptyObjectFileGenerator.getInstance();
        }
        return instance;
    }

    private VkWaylandConfiguration configuration;
    private VkLibraryCodeGenerator vkLibraryCodeGenerator;
    private EmptyObjectFileGenerator emptyObjectFileGenerator;

    private VkWaylandFileGenerator() {
    }

    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        List<File> files = vkLibraryCodeGenerator.generateFiles(root, configuration);
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("WlDisplay", "wl_display", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("WlSurface", "wl_surface", configuration));
        return files;
    }
}
