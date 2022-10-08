package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.c.*;
import cz.mg.vulkantransformator.services.translator.c.code.*;
import cz.mg.vulkantransformator.services.translator.c.code.types.*;
import cz.mg.vulkantransformator.services.translator.vk.*;
import cz.mg.vulkantransformator.services.translator.vk.code.*;

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
                CLibraryGenerator.getInstance(),
                CValidatorGenerator.getInstance()
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
            instance.cConfiguration = CLibraryConfiguration.getInstance();
            instance.vkConfiguration = VkLibraryConfiguration.getInstance();
        }
        return instance;
    }

    private List<CGenerator> generators;
    private List<VkTranslator> translators;
    private VkConstantTranslator constantTranslator;
    private VkFunctionsTranslator functionsTranslator;
    private VkLibraryGenerator libraryGenerator;
    private MakefileGenerator makefileGenerator;
    private CLibraryConfiguration cConfiguration;
    private VkLibraryConfiguration vkConfiguration;

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
                    Path.of(cConfiguration.getDirectory(), generator.getName() + ".java"),
                    generator.generateJava()
                )
            );

            files.addLast(
                new File(
                    Path.of(cConfiguration.getDirectory(), generator.getName() + ".c"),
                    generator.generateNativeC()
                )
            );

            files.addLast(
                new File(
                    Path.of(cConfiguration.getDirectory(), generator.getName() + ".h"),
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
                        Path.of(vkConfiguration.getDirectory(), component.getName() + ".java"),
                        translator.translateJava(index, component)
                    )
                );

                files.addLast(
                    new File(
                        Path.of(vkConfiguration.getDirectory(), component.getName() + ".c"),
                        translator.translateNative(index, component)
                    )
                );
            }
        }

        files.addLast(
            new File(
                Path.of(vkConfiguration.getDirectory(), constantTranslator.getName() + ".java"),
                constantTranslator.translateJava(index, root)
            )
        );

        files.addLast(
            new File(
                Path.of(vkConfiguration.getDirectory(), constantTranslator.getName() + ".c"),
                constantTranslator.translateNative(index, root)
            )
        );

        files.addLast(
            new File(
                Path.of(vkConfiguration.getDirectory(), functionsTranslator.getName() + ".java"),
                functionsTranslator.translateJava(index, root)
            )
        );

        files.addLast(
            new File(
                Path.of(vkConfiguration.getDirectory(), functionsTranslator.getName() + ".c"),
                functionsTranslator.translateNative(index, root)
            )
        );

        files.addLast(
            new File(
                Path.of(vkConfiguration.getDirectory(), libraryGenerator.getName() + ".java"),
                libraryGenerator.generateJava()
            )
        );

        return removeEmptyFiles(files);
    }

    private @Mandatory List<File> createMakeFiles(@Mandatory List<File> cFiles, @Mandatory List<File> vulkanFiles) {
        List<File> files = new List<>();

        files.addLast(
            new File(
                Path.of(cConfiguration.getDirectory(), "makefile"),
                makefileGenerator.create(
                    cFiles,
                    cConfiguration.getLibraryName(),
                    new List<>(JAVA_DIRECTORY, JAVA_DIRECTORY_MD),
                    new List<>()
                )
            )
        );

        files.addLast(
            new File(
                Path.of(vkConfiguration.getDirectory(), "makefile"),
                makefileGenerator.create(
                    vulkanFiles,
                    vkConfiguration.getLibraryName(),
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
