package cz.mg.vulkantransformator.services.translator.c.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.c.CGenerator;

public @Service class CInt16Generator implements CGenerator {
    private static @Optional CInt16Generator instance;

    public static @Mandatory CInt16Generator getInstance() {
        if (instance == null) {
            instance = new CInt16Generator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CInt16Generator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CInt16";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "short");
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return typeGenerator.generateNative(getName(), "jshort", "int16_t");
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
