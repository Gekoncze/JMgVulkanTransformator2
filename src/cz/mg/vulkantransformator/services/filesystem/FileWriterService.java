package cz.mg.vulkantransformator.services.filesystem;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

    public void save(@Mandatory String path, @Mandatory List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (String line : lines) {
                writer.write(line);
                writer.write("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
