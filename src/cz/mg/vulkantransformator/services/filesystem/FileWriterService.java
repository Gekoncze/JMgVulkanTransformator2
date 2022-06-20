package cz.mg.vulkantransformator.services.filesystem;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.vulkantransformator.entities.filesystem.File;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getName()))) {
            for (String line : file.getLines()) {
                writer.write(line);
                writer.write("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
