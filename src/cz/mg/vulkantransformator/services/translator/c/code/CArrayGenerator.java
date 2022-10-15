package cz.mg.vulkantransformator.services.translator.c.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.c.CConfiguration;

public @Service class CArrayGenerator implements CGenerator {
    private static @Optional CArrayGenerator instance;

    public static @Mandatory CArrayGenerator getInstance() {
        if (instance == null) {
            instance = new CArrayGenerator();
            instance.factoryGenerator = CFactoryGenerator.getInstance();
            instance.objectGenerator = CObjectGenerator.getInstance();
            instance.configuration = CConfiguration.getInstance();
        }
        return instance;
    }

    private CFactoryGenerator factoryGenerator;
    private CObjectGenerator objectGenerator;
    private CConfiguration configuration;

    private CArrayGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CArray";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        String genericFactoryName = factoryGenerator.getName() + "<T>";
        return new List<>(
            "package " + configuration.getJavaPackage() + ";",
            "",
            "public class " + getName() + "<T> extends " + objectGenerator.getName() + " {",
            "    private final int count;",
            "    private final long size;",
            "    private final " + genericFactoryName + " factory;",
            "",
            "    public " + getName() + "(long address, int count, long size, " + genericFactoryName + " factory) {",
            "        super(address);",
            "        this.count = count;",
            "        this.size = size;",
            "        this.factory = factory;",
            "    }",
            "",
            "    public T get(int i) {",
            "        if (i >= 0 && i < count) {",
            "            return factory.create(CPointer.offset(address, i, size));",
            "        } else {",
            "            throw new ArrayIndexOutOfBoundsException(i + \" out of \" + count);",
            "        }",
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
