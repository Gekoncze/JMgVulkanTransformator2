package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.components.StringJoiner;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.file.File;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.JavaConfiguration;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.MakefileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.Index;

import java.nio.file.Path;

@SuppressWarnings({"rawtypes", "unchecked"})
public @Service class VkComponentFileGenerator {
    private static volatile @Service VkComponentFileGenerator instance;

    public static @Mandatory VkComponentFileGenerator getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
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
            }
        }
        return instance;
    }

    private @Service List<VkTranslator> translators;
    private @Service VkConstantsTranslator constantTranslator;
    private @Service MakefileGenerator makefileGenerator;
    private @Service CodeGenerator codeGenerator;
    private @Service JavaConfiguration javaConfiguration;

    private VkComponentFileGenerator() {
    }

    public @Mandatory List<File> generateFiles(
        @Mandatory VkRoot root,
        @Mandatory LibraryConfiguration configuration
    ) {
        Index index = new Index(root);

        List<File> files = new List<>();

        Map<Class, VkTranslator> translatorMap = new Map<>();
        for (VkTranslator translator : translators) {
            translatorMap.set(translator.targetClass(), translator);
        }

        for (VkComponent component : root.getComponents()) {
            VkTranslator translator = translatorMap.getOptional(component.getClass());
            if (translator != null) {
                files.addLast(
                    new File(
                        Path.of(configuration.getDirectory(), translator.getJavaName(component) + ".java"),
                        join(translator.translateJava(index, component, configuration))
                    )
                );

                files.addLast(
                    new File(
                        Path.of(configuration.getDirectory(), translator.getJavaName(component) + ".c"),
                        join(translator.translateNative(index, component, configuration))
                    )
                );

                files.addLast(
                    new File(
                        Path.of(configuration.getDirectory(), translator.getJavaName(component) + ".h"),
                        join(translator.translateNativeHeader(index, component, configuration))
                    )
                );
            }
        }

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), constantTranslator.getName(configuration) + ".java"),
                join(constantTranslator.translateJava(index, root, configuration))
            )
        );

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), constantTranslator.getName(configuration) + ".c"),
                join(constantTranslator.translateNative(index, root, configuration))
            )
        );

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), getLibraryName(configuration) + ".java"),
                join(codeGenerator.generateJavaLibraryClass(configuration, getLibraryName(configuration)))
            )
        );

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), "makefile"),
                join(
                    makefileGenerator.create(
                        configuration.getJavaLibraryName(),
                        configuration.getNativeLibraryName(),
                        new List<>(
                            javaConfiguration.getJavaDirectory(),
                            javaConfiguration.getJavaDirectoryMd()
                        ),
                        configuration.getNativeLibraryDependencies(),
                        new List<>(
                            "-Wl,--no-as-needed"
                        ),
                        configuration.getJavaPackage(),
                        configuration.getJavaLibraryDependencies()
                    )
                )
            )
        );

        return files;
    }

    public @Mandatory String getLibraryName(@Mandatory LibraryConfiguration configuration) {
        return "Vk" + configuration.getSubModulePrefix() + "Library";
    }

    private @Mandatory String join(@Mandatory List<String> lines) {
        return new StringJoiner<>(lines).withDelimiter("\n").join();
    }
}
