package cz.mg.vulkantransformator.services.translator.vk.android;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.vk.VkFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkLibraryCodeGenerator;

public @Service class VkAndroidFileGenerator {
    private static @Optional VkAndroidFileGenerator instance;

    public static @Mandatory VkAndroidFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkAndroidFileGenerator();
            instance.configuration = VkAndroidConfiguration.getInstance();
            instance.vkLibraryCodeGenerator = VkLibraryCodeGenerator.getInstance();
            instance.vkFileGenerator = VkFileGenerator.getInstance();
            instance.aNativeWindowGenerator = ANativeWindowGenerator.getInstance();
            instance.aHardwareBufferGenerator = AHardwareBufferGenerator.getInstance();
        }
        return instance;
    }

    private VkAndroidConfiguration configuration;
    private VkLibraryCodeGenerator vkLibraryCodeGenerator;
    private VkFileGenerator vkFileGenerator;
    private ANativeWindowGenerator aNativeWindowGenerator;
    private AHardwareBufferGenerator aHardwareBufferGenerator;

    private VkAndroidFileGenerator() {
    }

    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        List<File> files = vkLibraryCodeGenerator.generateFiles(root, configuration);
        files.addCollectionLast(vkFileGenerator.generateFiles(aNativeWindowGenerator, configuration));
        files.addCollectionLast(vkFileGenerator.generateFiles(aHardwareBufferGenerator, configuration));
        return files;
    }
}
