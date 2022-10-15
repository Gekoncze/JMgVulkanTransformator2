package cz.mg.vulkantransformator.services.translator.vk.xcb;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.EmptyObjectFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkLibraryCodeGenerator;

public @Service class VkXcbFileGenerator {
    private static @Optional VkXcbFileGenerator instance;

    public static @Mandatory VkXcbFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkXcbFileGenerator();
            instance.configuration = VkXcbConfiguration.getInstance();
            instance.vkLibraryCodeGenerator = VkLibraryCodeGenerator.getInstance();
            instance.emptyObjectFileGenerator = EmptyObjectFileGenerator.getInstance();
        }
        return instance;
    }

    private VkXcbConfiguration configuration;
    private VkLibraryCodeGenerator vkLibraryCodeGenerator;
    private EmptyObjectFileGenerator emptyObjectFileGenerator;

    private VkXcbFileGenerator() {
    }

    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        List<File> files = vkLibraryCodeGenerator.generateFiles(root, configuration);
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("XcbConnection", "xcb_connection_t", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("XcbVisualId", "xcb_visualid_t", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("XcbWindow", "xcb_window_t", configuration));
        return files;
    }
}
