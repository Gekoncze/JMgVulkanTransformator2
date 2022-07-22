package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.generators.*;
import cz.mg.vulkantransformator.services.translator.generators.types.*;
import cz.mg.vulkantransformator.services.translator.vk.VkStructureTranslator;
import cz.mg.vulkantransformator.services.translator.vk.VkTranslator;
import cz.mg.vulkantransformator.services.translator.vk.VkUnionTranslator;

@SuppressWarnings({"rawtypes", "unchecked"})
public @Service class VulkanTranslator {
    private static VulkanTranslator instance;

    public static @Mandatory VulkanTranslator getInstance() {
        if (instance == null) {
            instance = new VulkanTranslator();
            instance.memoryGenerator = CMemoryGenerator.getInstance();
            instance.pointerGenerator = CPointerGenerator.getInstance();
            instance.arrayGenerator = CArrayGenerator.getInstance();
            instance.objectGenerator = CObjectGenerator.getInstance();
            instance.factoryGenerator = CFactoryGenerator.getInstance();
            instance.charGenerator = CCharGenerator.getInstance();
            instance.uInt8Generator = CUInt8Generator.getInstance();
            instance.uInt16Generator = CUInt16Generator.getInstance();
            instance.uInt32Generator = CUInt32Generator.getInstance();
            instance.uInt64Generator = CUInt64Generator.getInstance();
            instance.int8Generator = CInt8Generator.getInstance();
            instance.int16Generator = CInt16Generator.getInstance();
            instance.int32Generator = CInt32Generator.getInstance();
            instance.int64Generator = CInt64Generator.getInstance();
            instance.floatGenerator = CFloatGenerator.getInstance();
            instance.doubleGenerator = CDoubleGenerator.getInstance();
            instance.structureTranslator = VkStructureTranslator.getInstance();
            instance.unionTranslator = VkUnionTranslator.getInstance();
        }
        return instance;
    }

    private CMemoryGenerator memoryGenerator;
    private CPointerGenerator pointerGenerator;
    private CArrayGenerator arrayGenerator;
    private CObjectGenerator objectGenerator;
    private CFactoryGenerator factoryGenerator;

    private CCharGenerator charGenerator;
    private CUInt8Generator uInt8Generator;
    private CUInt16Generator uInt16Generator;
    private CUInt32Generator uInt32Generator;
    private CUInt64Generator uInt64Generator;
    private CInt8Generator int8Generator;
    private CInt16Generator int16Generator;
    private CInt32Generator int32Generator;
    private CInt64Generator int64Generator;
    private CFloatGenerator floatGenerator;
    private CDoubleGenerator doubleGenerator;

    private VkStructureTranslator structureTranslator;
    private VkUnionTranslator unionTranslator;

    private VulkanTranslator() {
    }

    public @Mandatory List<File> export(@Mandatory VkRoot root) {
        List<Generator> generators = new List<>(
            memoryGenerator,
            pointerGenerator,
            arrayGenerator,
            objectGenerator,
            factoryGenerator,
            charGenerator,
            uInt8Generator,
            uInt16Generator,
            uInt32Generator,
            uInt64Generator,
            int8Generator,
            int16Generator,
            int32Generator,
            int64Generator,
            floatGenerator,
            doubleGenerator
        );

        List<VkTranslator> translators = new List<>(
            structureTranslator,
            unionTranslator
            // TODO - add more translators
        );

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
                        createJavaFile(component.getName(), translator.translateJava(index, component), true)
                    );

                    files.addLast(
                        createNativeFileC(component.getName(), translator.translateNative(index, component), true)
                    );
                }
            }
        }

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
