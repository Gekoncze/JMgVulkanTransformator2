package cz.mg.vulkantransformator.services.translator.vk.wayland;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.vk.VkFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkLibraryCodeGenerator;

public @Service class VkWaylandFileGenerator {
    private static @Optional VkWaylandFileGenerator instance;

    public static @Mandatory VkWaylandFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkWaylandFileGenerator();
            instance.configuration = VkWaylandConfiguration.getInstance();
            instance.vkLibraryCodeGenerator = VkLibraryCodeGenerator.getInstance();
            instance.vkFileGenerator = VkFileGenerator.getInstance();
            instance.wlDisplayGenerator = WlDisplayGenerator.getInstance();
            instance.wlSurfaceGenerator = WlSurfaceGenerator.getInstance();
        }
        return instance;
    }

    private VkWaylandConfiguration configuration;
    private VkLibraryCodeGenerator vkLibraryCodeGenerator;
    private VkFileGenerator vkFileGenerator;
    private WlDisplayGenerator wlDisplayGenerator;
    private WlSurfaceGenerator wlSurfaceGenerator;

    private VkWaylandFileGenerator() {
    }

    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        List<File> files = vkLibraryCodeGenerator.generateFiles(root, configuration);
        files.addCollectionLast(vkFileGenerator.generateFiles(wlDisplayGenerator, configuration));
        files.addCollectionLast(vkFileGenerator.generateFiles(wlSurfaceGenerator, configuration));
        return files;
    }
}
