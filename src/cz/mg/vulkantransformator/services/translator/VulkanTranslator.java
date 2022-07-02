package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.vk.generators.CArrayGenerator;
import cz.mg.vulkantransformator.services.translator.vk.generators.VkGenerator;
import cz.mg.vulkantransformator.services.translator.vk.generators.CMemoryGenerator;
import cz.mg.vulkantransformator.services.translator.vk.generators.CPointerGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkStructureTranslator;
import cz.mg.vulkantransformator.services.translator.vk.VkTranslator;

@SuppressWarnings({"rawtypes", "unchecked"})
public @Service class VulkanTranslator {
    private static VulkanTranslator instance;

    public static @Mandatory VulkanTranslator getInstance() {
        if (instance == null) {
            instance = new VulkanTranslator();
            instance.memoryGenerator = CMemoryGenerator.getInstance();
            instance.pointerGenerator = CPointerGenerator.getInstance();
            instance.arrayGenerator = CArrayGenerator.getInstance();
            instance.structureTranslator = VkStructureTranslator.getInstance();
        }
        return instance;
    }

    private CMemoryGenerator memoryGenerator;
    private CPointerGenerator pointerGenerator;
    private CArrayGenerator arrayGenerator;
    private VkStructureTranslator structureTranslator;

    private VulkanTranslator() {
    }

    public @Mandatory List<File> export(@Mandatory VkRoot root) {
        List<VkGenerator> generators = new List<>(
            memoryGenerator,
            pointerGenerator,
            arrayGenerator
        );

        List<VkTranslator> translators = new List<>(
            structureTranslator
        );

        Index index = new Index(root);

        List<File> files = new List<>();

        for (VkGenerator generator : generators) {
            files.addLast(
                createJavaFile(generator.getName(), generator.generateJava())
            );

            files.addLast(
                createNativeFileC(generator.getName(), generator.generateNativeC())
            );

            files.addLast(
                createNativeFileH(generator.getName(), generator.generateNativeH())
            );
        }

        for (VkComponent component : root.getComponents()) {
            for (VkTranslator translator : translators) {
                if (translator.targetClass().equals(component.getClass())) {
                    files.addLast(
                        createJavaFile(component.getName(), translator.translateJava(index, component))
                    );

                    files.addLast(
                        createNativeFileC(component.getName(), translator.translateNative(index, component))
                    );
                }
            }
        }

        return files;
    }

    private @Mandatory File createJavaFile(@Mandatory String name, @Mandatory List<String> lines) {
        String filename = Configuration.DIRECTORY + "/" + name + ".java";
        return new File(filename, lines);
    }

    private @Mandatory File createNativeFileC(@Mandatory String name, @Mandatory List<String> lines) {
        String filename = Configuration.DIRECTORY + "/" + name + ".c";
        return new File(filename, lines);
    }

    private @Mandatory File createNativeFileH(@Mandatory String name, @Mandatory List<String> lines) {
        String filename = Configuration.DIRECTORY + "/" + name + ".h";
        return new File(filename, lines);
    }
}
