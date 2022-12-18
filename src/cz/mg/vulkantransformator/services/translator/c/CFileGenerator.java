package cz.mg.vulkantransformator.services.translator.c;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.services.translator.JavaConfiguration;
import cz.mg.vulkantransformator.services.translator.MakefileGenerator;
import cz.mg.vulkantransformator.services.translator.c.code.*;
import cz.mg.vulkantransformator.services.translator.c.code.types.*;

import java.nio.file.Path;

public @Service class CFileGenerator {
    private static @Optional CFileGenerator instance;

    public static @Mandatory CFileGenerator getInstance() {
        if (instance == null) {
            instance = new CFileGenerator();
            instance.configuration = CConfiguration.getInstance();
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
            instance.javaConfiguration = JavaConfiguration.getInstance();
        }
        return instance;
    }

    private CConfiguration configuration;
    private List<CGenerator> generators;
    private MakefileGenerator makefileGenerator;
    private JavaConfiguration javaConfiguration;

    private CFileGenerator() {
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
                    generator.generateNative()
                )
            );

            files.addLast(
                new File(
                    Path.of(configuration.getDirectory(), generator.getName() + ".h"),
                    generator.generateNativeHeader()
                )
            );
        }

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), "makefile"),
                makefileGenerator.create(
                    configuration.getJavaLibraryName(),
                    configuration.getNativeLibraryName(),
                    new List<>(
                        javaConfiguration.getJavaDirectory(),
                        javaConfiguration.getJavaDirectoryMd()
                    ),
                    new List<>(),
                    new List<>()
                )
            )
        );

        return files;
    }
}
