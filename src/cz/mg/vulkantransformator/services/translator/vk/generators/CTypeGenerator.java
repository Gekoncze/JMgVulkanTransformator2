package cz.mg.vulkantransformator.services.translator.vk.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class CTypeGenerator implements VkGenerator {
    private static @Optional CTypeGenerator instance;

    public static @Mandatory CTypeGenerator getInstance() {
        if (instance == null) {
            instance = new CTypeGenerator();
        }
        return instance;
    }

    private CTypeGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CType";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + Configuration.PACKAGE + ";",
            "",
            "public class " + getName() + "<T> {",
            "    private final long size;",
            "    private final CFactory<T> factory;",
            "",
            "    public " + getName() + "(long size, CFactory<T> factory) {",
            "        this.size = size;",
            "        this.factory = factory;",
            "    }",
            "",
            "    public long getSize() {",
            "        return size;",
            "    }",
            "",
            "    public CFactory<T> getFactory() {",
            "        return factory;",
            "    }",
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
