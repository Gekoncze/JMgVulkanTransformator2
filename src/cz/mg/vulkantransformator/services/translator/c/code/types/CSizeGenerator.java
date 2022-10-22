package cz.mg.vulkantransformator.services.translator.c.code.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.c.code.CGenerator;

public @Service class CSizeGenerator implements CGenerator {
    private static @Optional CSizeGenerator instance;

    public static @Mandatory CSizeGenerator getInstance() {
        if (instance == null) {
            instance = new CSizeGenerator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CSizeGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CSize";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "long");
    }

    @Override
    public @Mandatory List<String> generateNative() {
        return typeGenerator.generateNative(getName(), "jlong", "size_t");
    }

    @Override
    public @Mandatory List<String> generateNativeHeader() {
        return new List<>();
    }
}
