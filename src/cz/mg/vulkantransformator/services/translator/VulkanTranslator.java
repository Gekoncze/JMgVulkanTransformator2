package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.index.Index;
import cz.mg.vulkantransformator.services.translator.vk.VkStructureTranslator;
import cz.mg.vulkantransformator.services.translator.vk.VkTranslator;

@SuppressWarnings({"rawtypes", "unchecked"})
public @Service class VulkanTranslator {
    private static VulkanTranslator instance;

    public static @Mandatory VulkanTranslator getInstance() {
        if (instance == null) {
            instance = new VulkanTranslator();
            instance.structureTranslator = VkStructureTranslator.getInstance();
        }
        return instance;
    }

    private VkStructureTranslator structureTranslator;

    private VulkanTranslator() {
    }

    public @Mandatory List<File> export(@Mandatory VkRoot root) {
        List<VkTranslator> translators = new List<>(
            structureTranslator
        );

        Index index = new Index(root);

        List<File> files = new List<>();
        for (VkComponent component : root.getComponents()) {
            for (VkTranslator translator : translators) {
                if (translator.targetClass().equals(component.getClass())) {
                    files.addLast(createJavaFile(component, translator.translateJava(index, component)));
                    files.addLast(createNativeFile(component, translator.translateNative(index, component)));
                }
            }
        }
        return files;
    }

    private @Mandatory File createJavaFile(@Mandatory VkComponent component, @Mandatory List<String> lines) {
        String name = Configuration.DIRECTORY + "/" + component.getName() + ".java";
        return new File(name, lines);
    }

    private @Mandatory File createNativeFile(@Mandatory VkComponent component, @Mandatory List<String> lines) {
        String name = Configuration.DIRECTORY + "/" + component.getName() + ".c";
        return new File(name, lines);
    }
}
