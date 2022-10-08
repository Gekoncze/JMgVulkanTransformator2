package cz.mg.vulkantransformator.services.translator.c.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.c.CLibraryConfiguration;

public @Service class CFactoryGenerator implements CGenerator {
    private static @Optional CFactoryGenerator instance;

    public static @Mandatory CFactoryGenerator getInstance() {
        if (instance == null) {
            instance = new CFactoryGenerator();
            instance.configuration = CLibraryConfiguration.getInstance();
        }
        return instance;
    }

    private CLibraryConfiguration configuration;

    private CFactoryGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CFactory";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + configuration.getJavaPackage() + ";",
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