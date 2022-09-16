package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;

public @Service class MakefileCreator {
    private static @Optional MakefileCreator instance;

    public static @Mandatory MakefileCreator getInstance() {
        if (instance == null) {
            instance = new MakefileCreator();
        }
        return instance;
    }

    private MakefileCreator() {
    }

    public @Mandatory File create(@Mandatory List<File> files) {
        throw new UnsupportedOperationException(); // TODO
    }
}
