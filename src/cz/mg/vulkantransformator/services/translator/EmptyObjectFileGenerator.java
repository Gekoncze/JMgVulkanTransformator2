package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;

import java.nio.file.Path;

public @Service class EmptyObjectFileGenerator {
    private static @Optional EmptyObjectFileGenerator instance;

    public static @Mandatory EmptyObjectFileGenerator getInstance() {
        if (instance == null) {
            instance = new EmptyObjectFileGenerator();
            instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
        }
        return instance;
    }

    private ObjectCodeGenerator objectCodeGenerator;

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
                generateJava(name, configuration)
            ),
            new File(
                Path.of(configuration.getDirectory(), name + ".c"),
                generateNative(name, nativeName, configuration)
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
}
