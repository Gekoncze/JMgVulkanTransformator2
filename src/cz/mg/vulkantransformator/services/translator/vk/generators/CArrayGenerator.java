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
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CArrayGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CArray";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        String genericTypeName = typeGenerator.getName() + "<T>";
        return new List<>(
            "package " + Configuration.PACKAGE + ";",
            "",
            "public class " + getName() + "<T> {",
            "    private final long address;",
            "    private final int count;",
            "    private final " + genericTypeName + " type;",
            "",
            "    public " + getName() + "(long address, int count, " + genericTypeName + " type) {",
            "        this.address = address;",
            "        this.count = count;",
            "        this.type = type;",
            "    }",
            "",
            "    public T get(int i) {",
            "        if (i >= 0 && i < count) {",
            "            return type.getFactory().create(address + i * type.getSize());",
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
