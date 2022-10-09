package cz.mg.vulkantransformator.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.entities.vulkan.VkVersion;
import cz.mg.vulkantransformator.services.filesystem.FileReaderService;
import cz.mg.vulkantransformator.services.filesystem.FileWriterService;
import cz.mg.vulkantransformator.services.parser.VulkanParser;
import cz.mg.vulkantransformator.services.translator.c.CLibraryCodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkLibraryCodeGenerator;

import java.nio.file.Path;

public @Service class VulkanTransformator {
    private static final @Mandatory String VULKAN_FILE_NAME = "vulkan_core.h";

    private static @Optional VulkanTransformator instance;

    public static @Mandatory VulkanTransformator getInstance() {
        if (instance == null) {
            instance = new VulkanTransformator();
            instance.fileReaderService = FileReaderService.getInstance();
            instance.fileWriterService = FileWriterService.getInstance();
            instance.vulkanParser = VulkanParser.getInstance();
            instance.cLibraryCodeGenerator = CLibraryCodeGenerator.getInstance();
            instance.vkLibraryCodeGenerator = VkLibraryCodeGenerator.getInstance();
        }
        return instance;
    }

    private FileReaderService fileReaderService;
    private FileWriterService fileWriterService;
    private VulkanParser vulkanParser;
    private CLibraryCodeGenerator cLibraryCodeGenerator;
    private VkLibraryCodeGenerator vkLibraryCodeGenerator;

    private VulkanTransformator() {
    }

    /**
     * @param inputDirectory source directory containing vulkan header files, usually "/usr/include/vulkan/"
     * @param outputDirectory destination directory where generated java and c code will be saved
     */
    public void transform(@Mandatory Path inputDirectory, @Mandatory Path outputDirectory) {
        VkVersion version = new VkVersion(1, 1);

        Path inputPath = inputDirectory.resolve(VULKAN_FILE_NAME).toAbsolutePath();
        File inputFile = new File(inputPath, null);
        fileReaderService.load(inputFile);

        VkRoot root = vulkanParser.parse(version, inputFile);

        List<File> files = new List<>();
        files.addCollectionLast(cLibraryCodeGenerator.generateFiles());
        files.addCollectionLast(vkLibraryCodeGenerator.generateFiles(root));

        for (File file : files) {
            if (file.getLines().count() > 0) {
                Path outputPath = outputDirectory.resolve(file.getPath());
                file.setPath(outputPath);
                fileWriterService.save(file);
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
