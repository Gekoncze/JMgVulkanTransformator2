package cz.mg.vulkantransformator.services.translator.c.code.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.c.code.CGenerator;

public @Service class CFloatGenerator implements CGenerator {
    private static @Optional CFloatGenerator instance;

    public static @Mandatory CFloatGenerator getInstance() {
        if (instance == null) {
            instance = new CFloatGenerator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CFloatGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CFloat";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "float");
    }

    @Override
    public @Mandatory List<String> generateNative() {
        return typeGenerator.generateNative(getName(), "jfloat", "float");
    }

    @Override
    public @Mandatory List<String> generateNativeHeader() {
        return new List<>();
    }
}
