package cz.mg.vulkantransformator.services.filesystem;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.vulkantransformator.entities.filesystem.File;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public @Service class FileWriterService {
    private static FileWriterService instance;

    public static @Mandatory FileWriterService getInstance() {
        if (instance == null) {
            instance = new FileWriterService();
        }
        return instance;
    }

    private FileWriterService() {
    }

    public void save(@Mandatory File file) {
        Path path = file.getPath();

        try {
            Files.createDirectories(path.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            for (String line : file.getLines()) {
                writer.write(line);
                writer.write("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
