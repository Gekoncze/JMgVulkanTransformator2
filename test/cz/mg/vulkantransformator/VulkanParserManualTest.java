package cz.mg.vulkantransformator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.file.File;
import cz.mg.file.FileReader;
import cz.mg.vulkantransformator.entities.vulkan.*;
import cz.mg.vulkantransformator.services.VulkanParser;

import java.nio.file.Path;

public @Test class VulkanParserManualTest {
    private static final Path PATH = Path.of("test/cz/mg/vulkantransformator/vulkan_core.h");

    public static void main(String[] args) {
        System.out.print("Running " + VulkanParserManualTest.class.getSimpleName() + " ... ");

        VulkanParserManualTest test = new VulkanParserManualTest();
        test.testTransformation();

        System.out.println("OK");
    }

    private final @Service FileReader reader = FileReader.getInstance();

    private void testTransformation() {
        VulkanParser parser = VulkanParser.getInstance();
        File file = new File(PATH, null);
        reader.read(file);
        VkRoot root = parser.parse(file);

        for (VkComponent component : root.getComponents()) {
            System.out.println("Found " + component.getClass().getSimpleName() + " " + component.getName() + ".");
        }
    }
}
