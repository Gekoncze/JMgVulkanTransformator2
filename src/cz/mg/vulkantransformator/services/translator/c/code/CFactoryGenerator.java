package cz.mg.vulkantransformator.services.translator.c.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.c.CConfiguration;

public @Service class CFactoryGenerator implements CGenerator {
    private static @Optional CFactoryGenerator instance;

    public static @Mandatory CFactoryGenerator getInstance() {
        if (instance == null) {
            instance = new CFactoryGenerator();
            instance.configuration = CConfiguration.getInstance();
        }
        return instance;
    }

    private CConfiguration configuration;

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
    public @Mandatory List<String> generateNative() {
        return new List<>();
    }

    @Override
    public @Mandatory List<String> generateNativeHeader() {
        return new List<>();
    }
}
