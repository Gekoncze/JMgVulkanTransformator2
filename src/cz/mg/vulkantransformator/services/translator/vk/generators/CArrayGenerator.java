package cz.mg.vulkantransformator.services.translator.vk.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class CArrayGenerator implements VkGenerator {
    private static @Optional CArrayGenerator instance;

    public static @Mandatory CArrayGenerator getInstance() {
        if (instance == null) {
            instance = new CArrayGenerator();
        }
        return instance;
    }

    private CArrayGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CArray";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + Configuration.PACKAGE + ";",
            "",
            "public class " + getName() + "<T> {",
            "    private final long address;",
            "    private final int count;",
            "    private final long size;",
            "    private final Factory<T> factory;",
            "",
            "    public " + getName() + "(long address, int count, long size, Factory<T> factory) {",
            "        this.address = address;",
            "        this.count = count;",
            "        this.size = size;",
            "        this.factory = factory;",
            "    }",
            "",
            "    public T get(int i) {",
            "        if (i >= 0 && i < count) {",
            "            return factory.create(address + i * size);",
            "        } else {",
            "            throw new ArrayIndexOutOfBoundsException(i + \" out of \" + count);",
            "        }",
            "    }",
            "",
            "    public interface Factory<T> {",
            "        T create(long address);",
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