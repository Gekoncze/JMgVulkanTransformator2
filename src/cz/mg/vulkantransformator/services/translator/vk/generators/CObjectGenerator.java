package cz.mg.vulkantransformator.services.translator.vk.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class CObjectGenerator implements VkGenerator {
    private static @Optional CObjectGenerator instance;

    public static @Mandatory CObjectGenerator getInstance() {
        if (instance == null) {
            instance = new CObjectGenerator();
        }
        return instance;
    }

    private CObjectGenerator() {
    }

    @Override
    public boolean isVulkan() {
        return false;
    }

    @Override
    public @Mandatory String getName() {
        return "CObject";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + Configuration.C_PACKAGE + ";",
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
    public @Mandatory List<String> generateNativeC() {
        return new List<>();
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
