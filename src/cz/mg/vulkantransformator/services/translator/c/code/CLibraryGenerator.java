package cz.mg.vulkantransformator.services.translator.c.code;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.c.CLibraryConfiguration;

public class CLibraryGenerator implements CGenerator {
    private static @Optional CLibraryGenerator instance;

    public static @Mandatory CLibraryGenerator getInstance() {
        if (instance == null) {
            instance = new CLibraryGenerator();
            instance.configuration = CLibraryConfiguration.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private CLibraryConfiguration configuration;
    private CodeGenerator codeGenerator;

    private CLibraryGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CLibrary";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return codeGenerator.generateJavaLibraryClass(configuration, getName());
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
