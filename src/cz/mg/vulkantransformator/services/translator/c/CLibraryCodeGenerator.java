package cz.mg.vulkantransformator.services.translator.c;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.services.translator.MakefileGenerator;
import cz.mg.vulkantransformator.services.translator.c.code.*;
import cz.mg.vulkantransformator.services.translator.c.code.types.*;

import java.nio.file.Path;

import static cz.mg.vulkantransformator.services.Configuration.JAVA_DIRECTORY;
import static cz.mg.vulkantransformator.services.Configuration.JAVA_DIRECTORY_MD;

public @Service class CLibraryCodeGenerator {
    private static @Optional CLibraryCodeGenerator instance;

    public static @Mandatory CLibraryCodeGenerator getInstance() {
        if (instance == null) {
            instance = new CLibraryCodeGenerator();
            instance.configuration = CLibraryConfiguration.getInstance();
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
            instance.makefileGenerator = MakefileGenerator.getInstance();
        }
        return instance;
    }

    private CLibraryConfiguration configuration;
    private List<CGenerator> generators;
    private MakefileGenerator makefileGenerator;

    private CLibraryCodeGenerator() {
    }

    public @Mandatory List<File> generateFiles() {
        List<File> files = new List<>();

        for (CGenerator generator : generators) {
            files.addLast(
                new File(
                    Path.of(configuration.getDirectory(), generator.getName() + ".java"),
                    generator.generateJava()
                )
            );

            files.addLast(
                new File(
                    Path.of(configuration.getDirectory(), generator.getName() + ".c"),
                    generator.generateNativeC()
                )
            );

            files.addLast(
                new File(
                    Path.of(configuration.getDirectory(), generator.getName() + ".h"),
                    generator.generateNativeH()
                )
            );
        }

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
