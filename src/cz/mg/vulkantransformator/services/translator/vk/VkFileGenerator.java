package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

import java.nio.file.Path;

public @Service class VkFileGenerator {
    private static @Optional VkFileGenerator instance;

    public static @Mandatory VkFileGenerator getInstance() {
        if (instance == null) {
            instance = new VkFileGenerator();
        }
        return instance;
    }

    private VkFileGenerator() {
    }

    public @Mandatory List<File> generateFiles(
        @Mandatory VkGenerator generator,
        @Mandatory LibraryConfiguration configuration
    ) {
        return new List<>(
            new File(
                Path.of(configuration.getDirectory(), generator.getName() + ".java"),
                generator.generateJava()
            ),
            new File(
                Path.of(configuration.getDirectory(), generator.getName() + ".c"),
                generator.generateNativeC()
            ),
            new File(
                Path.of(configuration.getDirectory(), generator.getName() + ".h"),
                generator.generateNativeH()
            )
        );
    }
}
