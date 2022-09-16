package cz.mg.vulkantransformator.services.translator.c.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.c.CGenerator;

public @Service class CInt8Generator implements CGenerator {
    private static @Optional CInt8Generator instance;

    public static @Mandatory CInt8Generator getInstance() {
        if (instance == null) {
            instance = new CInt8Generator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CInt8Generator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CInt8";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "byte");
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return typeGenerator.generateNative(getName(), "jbyte", "int8_t");
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
