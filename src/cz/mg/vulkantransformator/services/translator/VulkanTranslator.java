package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.generators.*;
import cz.mg.vulkantransformator.services.translator.generators.types.*;
import cz.mg.vulkantransformator.services.translator.vk.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public @Service class VulkanTranslator {
    private static VulkanTranslator instance;

    public static @Mandatory VulkanTranslator getInstance() {
        if (instance == null) {
            instance = new VulkanTranslator();
            instance.generators = new List<>(
                CMemoryGenerator.getInstance(),
                CPointerGenerator.getInstance(),
                CArrayGenerator.getInstance(),
                CObjectGenerator.getInstance(),
                CFactoryGenerator.getInstance(),
                CCharGenerator.getInstance(),
                CUInt8Generator.getInstance(),
                CUInt16Generator.getInstance(),
                CUInt32Generator.getInstance(),
                CUInt64Generator.getInstance(),
                CInt8Generator.getInstance(),
                CInt16Generator.getInstance(),
                CInt32Generator.getInstance(),
                CInt64Generator.getInstance(),
                CFloatGenerator.getInstance(),
                CDoubleGenerator.getInstance(),
                CSizeGenerator.getInstance()
            );
            instance.translators = new List<>(
                VkStructureTranslator.getInstance(),
                VkUnionTranslator.getInstance(),
                VkTypeTranslator.getInstance(),
                VkEnumTranslator.getInstance(),
                VkFlagsTranslator.getInstance(),
                VkFunctionPointerTranslator.getInstance()
            );
            instance.constantTranslator = VkConstantTranslator.getInstance();
        }
        return instance;
    }

    private List<Generator> generators;
    private List<VkTranslator> translators;
    private VkConstantTranslator constantTranslator;

    private VulkanTranslator() {
    }

    public @Mandatory List<File> export(@Mandatory VkRoot root) {
        Index index = new Index(root);

        List<File> files = new List<>();

        for (Generator generator : generators) {
            files.addLast(
                createJavaFile(generator.getName(), generator.generateJava(), generator.isVulkan())
            );

            files.addLast(
                createNativeFileC(generator.getName(), generator.generateNativeC(), generator.isVulkan())
            );

            files.addLast(
                createNativeFileH(generator.getName(), generator.generateNativeH(), generator.isVulkan())
            );
        }

        for (VkComponent component : root.getComponents()) {
            for (VkTranslator translator : translators) {
                if (translator.targetClass().equals(component.getClass())) {
                    files.addLast(
                        createJavaFile(
                            component.getName(),
                            translator.translateJava(index, component),
                            true
                        )
                    );

                    files.addLast(
                        createNativeFileC(
                            component.getName(),
                            translator.translateNative(index, component),
                            true
                        )
                    );
                }
            }
        }

        files.addLast(
            createJavaFile(
                constantTranslator.getName(),
                constantTranslator.translateJava(index, root),
                true
            )
        );

        files.addLast(
            createNativeFileC(
                constantTranslator.getName(),
                constantTranslator.translateNative(index, root),
                true
            )
        );

        return files;
    }

    private @Mandatory File createJavaFile(@Mandatory String name, @Mandatory List<String> lines, boolean isVulkan) {
        String directory = isVulkan ? Configuration.VULKAN_DIRECTORY : Configuration.C_DIRECTORY;
        String filename = directory + "/" + name + ".java";
        return new File(filename, lines);
    }

    private @Mandatory File createNativeFileC(@Mandatory String name, @Mandatory List<String> lines, boolean isVulkan) {
        String directory = isVulkan ? Configuration.VULKAN_DIRECTORY : Configuration.C_DIRECTORY;
        String filename = directory + "/" + name + ".c";
        return new File(filename, lines);
    }

    private @Mandatory File createNativeFileH(@Mandatory String name, @Mandatory List<String> lines, boolean isVulkan) {
        String directory = isVulkan ? Configuration.VULKAN_DIRECTORY : Configuration.C_DIRECTORY;
        String filename = directory + "/" + name + ".h";
        return new File(filename, lines);
    }
}
