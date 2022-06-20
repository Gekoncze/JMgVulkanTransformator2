package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.vk.VkTranslator;

@SuppressWarnings({"rawtypes", "unchecked"})
public @Service class VulkanTranslator {
    private static VulkanTranslator instance;

    public static @Mandatory VulkanTranslator getInstance() {
        if (instance == null) {
            instance = new VulkanTranslator();
        }
        return instance;
    }

    private VulkanTranslator() {
    }

    public @Mandatory List<File> export(@Mandatory VkRoot root) {
        List<VkTranslator> translators = new List<>(
            // TODO - add a list of vk translators
        );

        List<File> files = new List<>();
        for (VkComponent component : root.getComponents()) {
            for (VkTranslator translator : translators) {
                if (translator.targetClass().equals(component.getClass())) {
                    files.addLast(translator.translateJava(component));
                    files.addLast(translator.translateNative(component));
                }
            }
        }
        return files;
    }
}
