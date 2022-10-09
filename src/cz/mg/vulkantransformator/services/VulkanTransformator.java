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
import cz.mg.vulkantransformator.services.translator.c.CLibraryCodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkLibraryCodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkLibraryConfiguration;

import java.nio.file.Path;

import static cz.mg.vulkantransformator.services.Configuration.VULKAN_FILE_NAME;
import static cz.mg.vulkantransformator.services.Configuration.VULKAN_VERSION;

public @Service class VulkanTransformator {
    private static @Optional VulkanTransformator instance;

    public static @Mandatory VulkanTransformator getInstance() {
        if (instance == null) {
            instance = new VulkanTransformator();
            instance.fileReaderService = FileReaderService.getInstance();
            instance.fileWriterService = FileWriterService.getInstance();
            instance.vulkanParser = VulkanParser.getInstance();
            instance.cLibraryCodeGenerator = CLibraryCodeGenerator.getInstance();
            instance.vkLibraryCodeGenerator = VkLibraryCodeGenerator.getInstance();
            instance.vkLibraryConfiguration = VkLibraryConfiguration.getInstance();
        }
        return instance;
    }

    private FileReaderService fileReaderService;
    private FileWriterService fileWriterService;
    private VulkanParser vulkanParser;
    private CLibraryCodeGenerator cLibraryCodeGenerator;
    private VkLibraryCodeGenerator vkLibraryCodeGenerator;
    private VkLibraryConfiguration vkLibraryConfiguration;

    private VulkanTransformator() {
    }

    /**
     * @param inputDirectory source directory containing vulkan header files, usually "/usr/include/vulkan/"
     * @param outputDirectory destination directory where generated java and c code will be saved
     */
    public void transform(@Mandatory Path inputDirectory, @Mandatory Path outputDirectory) {
        generateNativeBridge(outputDirectory);
        generateVulkanBridge(inputDirectory, outputDirectory);
    }

    private void generateNativeBridge(@Mandatory Path outputDirectory) {
        List<File> files = cLibraryCodeGenerator.generateFiles();
        write(outputDirectory, files);
    }

    private void generateVulkanBridge(@Mandatory Path inputDirectory, @Mandatory Path outputDirectory) {
        File file = read(inputDirectory, VULKAN_FILE_NAME);
        VkRoot root = vulkanParser.parse(VULKAN_VERSION, file);
        List<File> files = vkLibraryCodeGenerator.generateFiles(root, vkLibraryConfiguration);
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
