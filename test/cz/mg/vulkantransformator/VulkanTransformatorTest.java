package cz.mg.vulkantransformator;

import cz.mg.annotations.classes.Test;
import cz.mg.vulkantransformator.entities.vulkan.*;
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
                System.out.println("Found structure " + structure.getName() + ".");
            }

            if (component instanceof VkUnion) {
                VkUnion union = (VkUnion) component;
                System.out.println("Found union " + union.getName() + ".");
            }

            if (component instanceof VkEnum) {
                VkEnum vkEnum = (VkEnum) component;
                System.out.println("Found enum " + vkEnum.getName() + ".");
            }
        }
    }
}
