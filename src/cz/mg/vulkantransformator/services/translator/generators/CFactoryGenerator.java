package cz.mg.vulkantransformator.services.translator.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class CFactoryGenerator implements Generator {
    private static @Optional CFactoryGenerator instance;

    public static @Mandatory CFactoryGenerator getInstance() {
        if (instance == null) {
            instance = new CFactoryGenerator();
        }
        return instance;
    }

    private CFactoryGenerator() {
    }

    @Override
    public boolean isVulkan() {
        return false;
    }

    @Override
    public @Mandatory String getName() {
        return "CFactory";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + Configuration.C_PACKAGE + ";",
            "",
            "public interface " + getName() + "<T> {",
            "    T create(long address);",
            "}"
        );
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return new List<>();
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
