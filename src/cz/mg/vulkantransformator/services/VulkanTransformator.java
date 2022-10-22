package cz.mg.vulkantransformator.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.filesystem.FileReaderService;
import cz.mg.vulkantransformator.services.filesystem.FileWriterService;
import cz.mg.vulkantransformator.services.parser.VulkanParser;
import cz.mg.vulkantransformator.services.translator.c.CFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.android.VkAndroidFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.core.VkCoreFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.ios.VkIosFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.macos.VkMacosFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.wayland.VkWaylandFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.windows.VkWindowsFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.xcb.VkXcbFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.xlib.VkXlibFileGenerator;

import java.nio.file.Path;

public @Service class VulkanTransformator {
    private static @Optional VulkanTransformator instance;

    public static @Mandatory VulkanTransformator getInstance() {
        if (instance == null) {
            instance = new VulkanTransformator();
            instance.fileReaderService = FileReaderService.getInstance();
            instance.fileWriterService = FileWriterService.getInstance();
            instance.vulkanParser = VulkanParser.getInstance();
            instance.cFileGenerator = CFileGenerator.getInstance();
            instance.vkLibraryFileGenerators = new List<>(
                VkCoreFileGenerator.getInstance(),
                VkXlibFileGenerator.getInstance(),
                VkXcbFileGenerator.getInstance(),
                VkWaylandFileGenerator.getInstance(),
                VkAndroidFileGenerator.getInstance(),
                VkIosFileGenerator.getInstance(),
                VkMacosFileGenerator.getInstance(),
                VkWindowsFileGenerator.getInstance()
            );
        }
        return instance;
    }

    private FileReaderService fileReaderService;
    private FileWriterService fileWriterService;
    private VulkanParser vulkanParser;
    private CFileGenerator cFileGenerator;
    private List<VkFileGenerator> vkLibraryFileGenerators;

    private VulkanTransformator() {
    }

    /**
     * @param inputDirectory source directory containing vulkan header files, usually "/usr/include/vulkan/"
     * @param outputDirectory destination directory where generated java and c code will be saved
     */
    public void transform(@Mandatory Path inputDirectory, @Mandatory Path outputDirectory) {
        generateNativeBridge(outputDirectory);
        for (VkFileGenerator generator : vkLibraryFileGenerators) {
            generateVulkanBridge(inputDirectory, outputDirectory, generator);
        }
    }

    private void generateNativeBridge(@Mandatory Path outputDirectory) {
        write(outputDirectory, cFileGenerator.generateFiles());
    }

    private void generateVulkanBridge(
        @Mandatory Path inputDirectory,
        @Mandatory Path outputDirectory,
        @Mandatory VkFileGenerator generator
    ) {
        File file = read(inputDirectory, generator.getSourceFileName());
        VkRoot root = vulkanParser.parse(file);
        List<File> files = generator.generateFiles(root);
        write(outputDirectory, files);
    }

    private @Mandatory File read(@Mandatory Path inputDirectory, @Mandatory String name) {
        Path path = inputDirectory.resolve(name).toAbsolutePath();
        File file = new File(path, null);
        fileReaderService.load(file);
        return file;
    }

    private void write(@Mandatory Path outputDirectory, @Mandatory File file) {
        Path outputPath = outputDirectory.resolve(file.getPath());
        file.setPath(outputPath);
        fileWriterService.save(file);
    }

    private void write(@Mandatory Path outputDirectory, @Mandatory List<File> files) {
        for (File file : files) {
            if (file.getLines().count() > 0) {
                write(outputDirectory, file);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Expected two arguments: input directory and output directory.");
            System.exit(-1);
        }

        VulkanTransformator.getInstance().transform(Path.of(args[0]), Path.of(args[1]));
    }
}
