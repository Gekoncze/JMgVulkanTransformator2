package cz.mg.vulkantransformator.services.translator.vk.windows;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.EmptyObjectFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.component.VkComponentFileGenerator;

public @Service class VkWindowsFileGenerator implements VkFileGenerator {
    private static @Optional VkWindowsFileGenerator instance;

    public static @Mandatory VkWindowsFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkWindowsFileGenerator();
            instance.configuration = VkWindowsConfiguration.getInstance();
            instance.vkLibraryCodeGenerator = VkComponentFileGenerator.getInstance();
            instance.emptyObjectFileGenerator = EmptyObjectFileGenerator.getInstance();
        }
        return instance;
    }

    private VkWindowsConfiguration configuration;
    private VkComponentFileGenerator vkLibraryCodeGenerator;
    private EmptyObjectFileGenerator emptyObjectFileGenerator;

    private VkWindowsFileGenerator() {
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return configuration.getSourceFileName();
    }

    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        List<File> files = vkLibraryCodeGenerator.generateFiles(root, configuration);
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("HANDLE", "HANDLE", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("HINSTANCE", "HINSTANCE", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("HWND", "HWND", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("HMONITOR", "HMONITOR", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("LPCWSTR", "LPCWSTR", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("SECURITY_ATTRIBUTES", "SECURITY_ATTRIBUTES", configuration));
        files.addCollectionLast(emptyObjectFileGenerator.generateFiles("DWORD", "DWORD", configuration));
        return files;
    }
}
