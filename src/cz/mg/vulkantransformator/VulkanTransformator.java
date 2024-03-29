package cz.mg.vulkantransformator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.file.File;
import cz.mg.file.FileReader;
import cz.mg.file.FileWriter;
import cz.mg.tokenizer.exceptions.TraceableException;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.VulkanParser;
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
    private static volatile @Service VulkanTransformator instance;

    public static @Service VulkanTransformator getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VulkanTransformator();
                    instance.fileReader = FileReader.getInstance();
                    instance.fileWriter = FileWriter.getInstance();
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
            }
        }
        return instance;
    }

    private @Service FileReader fileReader;
    private @Service FileWriter fileWriter;
    private @Service VulkanParser vulkanParser;
    private @Service CFileGenerator cFileGenerator;
    private @Service List<VkFileGenerator> vkLibraryFileGenerators;

    private VulkanTransformator() {
    }

    /**
     * @param inputDirectory source directory containing vulkan header files, usually "/usr/include/vulkan/"
     * @param outputDirectory destination directory where generated java and c code will be saved
     */
    public void transform(@Mandatory Path inputDirectory, @Mandatory Path outputDirectory) {
        generateNativeBridge(outputDirectory);
        for (VkFileGenerator generator : vkLibraryFileGenerators) {
            try {
                generateVulkanBridge(inputDirectory, outputDirectory, generator);
            } catch (TraceableException e) {
                throw new TraceableException(
                    e.getPosition(),
                    "In file " + generator.getSourceFileName() + ": " + e.getMessage(),
                    e
                );
            }
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
        fileReader.read(file);
        return file;
    }

    private void write(@Mandatory Path outputDirectory, @Mandatory File file) {
        Path outputPath = outputDirectory.resolve(file.getPath());
        file.setPath(outputPath);
        file.setContent(file.getContent() + "\n"); // TODO - only for testing, remove when no longer needed
        fileWriter.write(file);
    }

    private void write(@Mandatory Path outputDirectory, @Mandatory List<File> files) {
        for (File file : files) {
            if (file.getContent().length() > 0) {
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
