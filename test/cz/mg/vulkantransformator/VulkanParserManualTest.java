package cz.mg.vulkantransformator;

import cz.mg.annotations.classes.Test;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.*;
import cz.mg.vulkantransformator.services.filesystem.FileReaderService;
import cz.mg.vulkantransformator.services.parser.VulkanParser;

import java.nio.file.Path;

public @Test class VulkanParserManualTest {
    private static final Path PATH = Path.of("test/cz/mg/vulkantransformator/vulkan_core.h");

    public static void main(String[] args) {
        System.out.print("Running " + VulkanParserManualTest.class.getSimpleName() + " ... ");

        VulkanParserManualTest test = new VulkanParserManualTest();
        test.testTransformation();

        System.out.println("OK");
    }

    private void testTransformation() {
        FileReaderService reader = FileReaderService.getInstance();
        VulkanParser parser = VulkanParser.getInstance();
        File file = new File(PATH, null);
        reader.load(file);
        VkRoot root = parser.parse(file);

        for (VkComponent component : root.getComponents()) {
            System.out.println("Found " + component.getClass().getSimpleName() + " " + component.getName() + ".");
        }
    }
}
