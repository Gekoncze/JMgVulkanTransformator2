package cz.mg.vulkantransformator.services.translator.c;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public class CLibraryGenerator implements CGenerator {
    private static @Optional CLibraryGenerator instance;

    public static @Mandatory CLibraryGenerator getInstance() {
        if (instance == null) {
            instance = new CLibraryGenerator();
        }
        return instance;
    }

    private CLibraryGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CLibrary";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + Configuration.C_PACKAGE + ";",
            "",
            "public class " + getName() + " {",
            "    public static final String NAME = \"" + Configuration.C_LIBRARY + "\";",
            "",
            "    public static void load() {",
            "        System.loadLibrary(NAME);",
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
