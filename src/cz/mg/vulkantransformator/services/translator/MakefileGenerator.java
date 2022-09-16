package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;

public @Service class MakefileGenerator {
    private static @Optional MakefileGenerator instance;

    public static @Mandatory MakefileGenerator getInstance() {
        if (instance == null) {
            instance = new MakefileGenerator();
        }
        return instance;
    }

    private MakefileGenerator() {
    }

    public @Mandatory List<String> create(@Mandatory String name, @Mandatory List<File> files) {
        return new List<>(); // TODO
    }
}
