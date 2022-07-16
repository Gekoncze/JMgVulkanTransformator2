package cz.mg.vulkantransformator.services.translator.vk.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class CVoidGenerator implements VkGenerator {
    private static @Optional CVoidGenerator instance;

    public static @Mandatory CVoidGenerator getInstance() {
        if (instance == null) {
            instance = new CVoidGenerator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CVoidGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CVoid";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        String typeName = typeGenerator.getName();
        return new List<>(
            "package " + Configuration.PACKAGE + ";",
            "",
            "public class " + getName() + " {",
            "    public static final " + typeName + "<" + getName() + "> TYPE = new " + typeName + "<>(",
            "        1, (a) -> { throw new RuntimeException(\"Cannot create void.\"); }",
            "    );",
            "",
            "    private final long address;",
            "",
            "    public " + getName() + "(long address) {",
            "        this.address = address;",
            "    }",
            "",
            "    public long getAddress() {",
            "        return address;",
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
