package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;
import cz.mg.file.File;

import java.nio.file.Path;

public @Service class EmptyObjectFileGenerator {
    private static volatile @Service EmptyObjectFileGenerator instance;

    public static @Service EmptyObjectFileGenerator getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new EmptyObjectFileGenerator();
                    instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
                    instance.joiner = StringJoiner.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service ObjectCodeGenerator objectCodeGenerator;
    private @Service StringJoiner joiner;

    private EmptyObjectFileGenerator() {
    }

    public @Mandatory List<File> generateFiles(
        @Mandatory String name,
        @Mandatory String nativeName,
        @Mandatory LibraryConfiguration configuration
    ) {
        return new List<>(
            new File(
                Path.of(configuration.getDirectory(), name + ".java"),
                join(generateJava(name, configuration))
            ),
            new File(
                Path.of(configuration.getDirectory(), name + ".c"),
                join(generateNative(name, nativeName, configuration))
            )
        );
    }

    private @Mandatory List<String> generateJava(
        @Mandatory String name,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = objectCodeGenerator.getCommonJavaHeading(name, configuration);
        lines.addCollectionLast(objectCodeGenerator.getCommonJavaFooter());
        return lines;
    }

    private @Mandatory List<String> generateNative(
        @Mandatory String name,
        @Mandatory String nativeName,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = objectCodeGenerator.getCommonNativeHeading(name, nativeName, null, configuration);
        lines.addCollectionLast(objectCodeGenerator.getCommonNativeFooter());
        return lines;
    }

    private @Mandatory String join(@Mandatory List<String> lines) {
        return joiner.join(lines, "\n");
    }
}
