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

import java.nio.file.Path;

import static cz.mg.vulkantransformator.services.translator.Configuration.*;

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
                CSizeGenerator.getInstance(),
                CStringGenerator.getInstance()
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
            instance.functionsTranslator = VkFunctionsTranslator.getInstance();
        }
        return instance;
    }

    private List<CGenerator> generators;
    private List<VkTranslator> translators;
    private VkConstantTranslator constantTranslator;
    private VkFunctionsTranslator functionsTranslator;

    private VulkanTranslator() {
    }

    public @Mandatory List<File> export(@Mandatory VkRoot root) {
        Index index = new Index(root);

        List<File> files = new List<>();

        for (CGenerator generator : generators) {
            files.addLast(
                new File(
                    Path.of(C_DIRECTORY, generator.getName() + ".java"),
                    generator.generateJava()
                )
            );

            files.addLast(
                new File(
                    Path.of(C_DIRECTORY, generator.getName() + ".c"),
                    generator.generateNativeC()
                )
            );

            files.addLast(
                new File(
                    Path.of(C_DIRECTORY, generator.getName() + ".h"),
                    generator.generateNativeH()
                )
            );
        }

        for (VkComponent component : root.getComponents()) {
            for (VkTranslator translator : translators) {
                if (translator.targetClass().equals(component.getClass())) {
                    files.addLast(
                        new File(
                            Path.of(VULKAN_DIRECTORY, component.getName() + ".java"),
                            translator.translateJava(index, component)
                        )
                    );

                    files.addLast(
                        new File(
                            Path.of(VULKAN_DIRECTORY, component.getName() + ".c"),
                            translator.translateNative(index, component)
                        )
                    );
                }
            }
        }

        files.addLast(
            new File(
                Path.of(VULKAN_DIRECTORY, constantTranslator.getName() + ".java"),
                constantTranslator.translateJava(index, root)
            )
        );

        files.addLast(
            new File(
                Path.of(VULKAN_DIRECTORY, constantTranslator.getName() + ".c"),
                constantTranslator.translateNative(index, root)
            )
        );

        files.addLast(
            new File(
                Path.of(VULKAN_DIRECTORY, functionsTranslator.getName() + ".java"),
                functionsTranslator.translateJava(index, root)
            )
        );

        files.addLast(
            new File(
                Path.of(VULKAN_DIRECTORY, functionsTranslator.getName() + ".c"),
                functionsTranslator.translateNative(index, root)
            )
        );

        return files;
    }
}
