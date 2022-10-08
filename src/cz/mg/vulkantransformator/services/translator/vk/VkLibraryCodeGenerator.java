package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.MakefileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.code.*;

import java.nio.file.Path;

import static cz.mg.vulkantransformator.services.translator.Configuration.JAVA_DIRECTORY;
import static cz.mg.vulkantransformator.services.translator.Configuration.JAVA_DIRECTORY_MD;

@SuppressWarnings({"rawtypes", "unchecked"})
public @Service class VkLibraryCodeGenerator {
    private static @Optional VkLibraryCodeGenerator instance;

    public static @Mandatory VkLibraryCodeGenerator getInstance() {
        if (instance == null) {
            instance = new VkLibraryCodeGenerator();
            instance.configuration = VkLibraryConfiguration.getInstance();
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

    private VkLibraryConfiguration configuration;
    private List<VkTranslator> translators;
    private VkConstantTranslator constantTranslator;
    private VkFunctionsTranslator functionsTranslator;
    private VkLibraryGenerator libraryGenerator;
    private MakefileGenerator makefileGenerator;

    private VkLibraryCodeGenerator() {
    }

    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
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
                        Path.of(configuration.getDirectory(), component.getName() + ".java"),
                        translator.translateJava(index, component)
                    )
                );

                files.addLast(
                    new File(
                        Path.of(configuration.getDirectory(), component.getName() + ".c"),
                        translator.translateNative(index, component)
                    )
                );
            }
        }

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), constantTranslator.getName() + ".java"),
                constantTranslator.translateJava(index, root)
            )
        );

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), constantTranslator.getName() + ".c"),
                constantTranslator.translateNative(index, root)
            )
        );

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), functionsTranslator.getName() + ".java"),
                functionsTranslator.translateJava(index, root)
            )
        );

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), functionsTranslator.getName() + ".c"),
                functionsTranslator.translateNative(index, root)
            )
        );

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), libraryGenerator.getName() + ".java"),
                libraryGenerator.generateJava()
            )
        );

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), "makefile"),
                makefileGenerator.create(
                    files,
                    configuration.getLibraryName(),
                    new List<>(JAVA_DIRECTORY, JAVA_DIRECTORY_MD),
                    new List<>()
                )
            )
        );

        return files;
    }
}
