package cz.mg.vulkantransformator;

import cz.mg.annotations.classes.Test;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.entities.vulkan.VkStructure;
import cz.mg.vulkantransformator.entities.vulkan.VkVersion;
import cz.mg.vulkantransformator.services.filesystem.FileReaderService;
import cz.mg.vulkantransformator.services.parser.VulkanParser;

public @Test class VulkanTransformatorTest {
    private static final String PATH = "test/cz/mg/vulkantransformator/vulkan_core.h";

    public static void main(String[] args) {
        System.out.print("Running " + VulkanTransformatorTest.class.getSimpleName() + " ... ");

        VulkanTransformatorTest test = new VulkanTransformatorTest();
        test.testTransformation();

        System.out.println("OK");
    }

    private void testTransformation() {
        FileReaderService loader = FileReaderService.getInstance();
        VulkanParser parser = VulkanParser.getInstance();
        VkRoot root = parser.parse(new VkVersion(1, 0), loader.load(PATH));

        for (VkComponent component : root.getComponents()) {
            if (component instanceof VkStructure) {
                VkStructure structure = (VkStructure) component;
                System.out.println("Found structure " + structure.getName() + "!");
            }

        }
    }
}
