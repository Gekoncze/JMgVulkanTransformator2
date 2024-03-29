package cz.mg.vulkantransformator.services.translator.c.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.c.CConfiguration;

public @Service class CObjectGenerator implements CGenerator {
    private static @Optional CObjectGenerator instance;

    public static @Mandatory CObjectGenerator getInstance() {
        if (instance == null) {
            instance = new CObjectGenerator();
            instance.configuration = CConfiguration.getInstance();
        }
        return instance;
    }

    private CConfiguration configuration;

    private CObjectGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CObject";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + configuration.getJavaPackage() + ";",
            "",
            "public class " + getName() + " {",
            "    public static final long SIZE = 1;",
            "",
            "    protected final long address;",
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
    public @Mandatory List<String> generateNative() {
        return new List<>();
    }

    @Override
    public @Mandatory List<String> generateNativeHeader() {
        return new List<>();
    }
}
