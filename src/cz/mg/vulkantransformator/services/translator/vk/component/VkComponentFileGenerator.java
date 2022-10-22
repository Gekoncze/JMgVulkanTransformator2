package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.JavaConfiguration;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.MakefileGenerator;

import java.nio.file.Path;

@SuppressWarnings({"rawtypes", "unchecked"})
public @Service class VkComponentFileGenerator {
    private static @Optional VkComponentFileGenerator instance;

    public static @Mandatory VkComponentFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkComponentFileGenerator();
            instance.translators = new List<>(
                VkStructureTranslator.getInstance(),
                VkUnionTranslator.getInstance(),
                VkTypeTranslator.getInstance(),
                VkEnumTranslator.getInstance(),
                VkFlagsTranslator.getInstance(),
                VkFunctionTranslator.getInstance(),
                VkFunctionPointerTranslator.getInstance()
            );
            instance.constantTranslator = VkConstantsTranslator.getInstance();
            instance.makefileGenerator = MakefileGenerator.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
            instance.javaConfiguration = JavaConfiguration.getInstance();
        }
        return instance;
    }

    private List<VkTranslator> translators;
    private VkConstantsTranslator constantTranslator;
    private MakefileGenerator makefileGenerator;
    private CodeGenerator codeGenerator;
    private JavaConfiguration javaConfiguration;

    private VkComponentFileGenerator() {
    }

    public @Mandatory List<File> generateFiles(
        @Mandatory VkRoot root,
        @Mandatory LibraryConfiguration configuration
    ) {
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
                        Path.of(configuration.getDirectory(), translator.getJavaName(component) + ".java"),
                        translator.translateJava(index, component, configuration)
                    )
                );

                files.addLast(
                    new File(
                        Path.of(configuration.getDirectory(), translator.getJavaName(component) + ".c"),
                        translator.translateNative(index, component, configuration)
                    )
                );
            }
        }

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), constantTranslator.getName(configuration) + ".java"),
                constantTranslator.translateJava(index, root, configuration)
            )
        );

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), constantTranslator.getName(configuration) + ".c"),
                constantTranslator.translateNative(index, root, configuration)
            )
        );

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), getLibraryName(configuration) + ".java"),
                codeGenerator.generateJavaLibraryClass(configuration, getLibraryName(configuration))
            )
        );

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), "makefile"),
                makefileGenerator.create(
                    files,
                    configuration.getLibraryName(),
                    new List<>(
                        javaConfiguration.getJavaDirectory(),
                        javaConfiguration.getJavaDirectoryMd()
                    ),
                    new List<>(
                        "jmgc"
                    ),
                    new List<>(
                        "-Wl,--no-as-needed"
                    )
                )
            )
        );

        return files;
    }

    public @Mandatory String getLibraryName(@Mandatory LibraryConfiguration configuration) {
        return "Vk" + configuration.getSubModulePrefix() + "Library";
    }
}
