package cz.mg.vulkantransformator.services.translator.vk.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class CFactoryGenerator implements VkGenerator {
    private static @Optional CFactoryGenerator instance;

    public static @Mandatory CFactoryGenerator getInstance() {
        if (instance == null) {
            instance = new CFactoryGenerator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CFactoryGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CFactory";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        String genericTypeName = typeGenerator.getName() + "<T>";
        return new List<>(
            "package " + Configuration.PACKAGE + ";",
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
