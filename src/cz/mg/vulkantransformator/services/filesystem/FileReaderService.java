package cz.mg.vulkantransformator.services.filesystem;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public @Service class FileReaderService {
    private static FileReaderService instance;

    public static @Mandatory FileReaderService getInstance() {
        if (instance == null) {
            instance = new FileReaderService();
        }
        return instance;
    }

    private FileReaderService() {
    }

    public @Mandatory File load(@Mandatory String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            List<String> lines = new List<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.addLast(line);
            }
            return new File(path, lines);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
