package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.c.*;
import cz.mg.vulkantransformator.services.translator.c.types.*;
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
                CStringGenerator.getInstance(),
                CLibraryGenerator.getInstance()
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
            instance.libraryGenerator = VkLibraryGenerator.getInstance();
            instance.makefileGenerator = MakefileGenerator.getInstance();
        }
        return instance;
    }

    private List<CGenerator> generators;
    private List<VkTranslator> translators;
    private VkConstantTranslator constantTranslator;
    private VkFunctionsTranslator functionsTranslator;
    private VkLibraryGenerator libraryGenerator;
    private MakefileGenerator makefileGenerator;

    private VulkanTranslator() {
    }

    public @Mandatory List<File> export(@Mandatory VkRoot root) {
        List<File> cFiles = createCFiles();
        List<File> vulkanFiles = createVulkanFiles(root);
        List<File> makeFiles = createMakeFiles(cFiles, vulkanFiles);
        List<File> files = new List<>();
        files.addCollectionLast(cFiles);
        files.addCollectionLast(vulkanFiles);
        files.addCollectionLast(makeFiles);
        return files;
    }

    private @Mandatory List<File> createCFiles() {
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

        return removeEmptyFiles(files);
    }

    private @Mandatory List<File> createVulkanFiles(@Mandatory VkRoot root) {
        Index index = new Index(root);

        List<File> files = new List<>();

        Map<Class, VkTranslator> translatorMap = new Map<>(100);
        for (VkTranslator translator : translators) {
            translatorMap.set(translator.targetClass(), translator);
        }

        for (VkComponent component : root.getComponents()) {
            VkTranslator translator = translatorMap.getOptional(component.getClass());
            if (translator != null) {
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

        files.addLast(
            new File(
                Path.of(VULKAN_DIRECTORY, libraryGenerator.getName() + ".java"),
                libraryGenerator.generateJava()
            )
        );

        return removeEmptyFiles(files);
    }

    private @Mandatory List<File> createMakeFiles(@Mandatory List<File> cFiles, @Mandatory List<File> vulkanFiles) {
        List<File> files = new List<>();

        files.addLast(
            new File(
                Path.of(C_DIRECTORY, "makefile"),
                makefileGenerator.create(
                    cFiles,
                    C_LIBRARY,
                    new List<>(JAVA_DIRECTORY, JAVA_DIRECTORY_MD),
                    new List<>()
                )
            )
        );

        files.addLast(
            new File(
                Path.of(VULKAN_DIRECTORY, "makefile"),
                makefileGenerator.create(
                    vulkanFiles,
                    VULKAN_LIBRARY,
                    new List<>(JAVA_DIRECTORY, JAVA_DIRECTORY_MD),
                    new List<>()
                )
            )
        );

        return removeEmptyFiles(files);
    }

    private @Mandatory List<File> removeEmptyFiles(@Mandatory List<File> files) {
        List<File> newFiles = new List<>();
        for (File file : files) {
            if (file.getLines() != null) {
                if (file.getLines().count() > 0) {
                    newFiles.addLast(file);
                }
            }
        }
        return newFiles;
    }
}
