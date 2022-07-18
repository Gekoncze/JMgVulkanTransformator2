package cz.mg.vulkantransformator;

import cz.mg.annotations.classes.Test;
import cz.mg.vulkantransformator.entities.vulkan.*;
import cz.mg.vulkantransformator.services.filesystem.FileReaderService;
import cz.mg.vulkantransformator.services.parser.VulkanParser;

public @Test class VulkanParserManualTest {
    private static final String PATH = "test/cz/mg/vulkantransformator/vulkan_core.h";

    public static void main(String[] args) {
        System.out.print("Running " + VulkanParserManualTest.class.getSimpleName() + " ... ");

        VulkanParserManualTest test = new VulkanParserManualTest();
        test.testTransformation();

        System.out.println("OK");
    }

    private void testTransformation() {
        FileReaderService loader = FileReaderService.getInstance();
        VulkanParser parser = VulkanParser.getInstance();
        VkRoot root = parser.parse(new VkVersion(1, 0), loader.load(PATH));

        for (VkComponent component : root.getComponents()) {
            System.out.println("Found " + component.getClass().getSimpleName() + " " + component.getName() + ".");
        }
    }
}
