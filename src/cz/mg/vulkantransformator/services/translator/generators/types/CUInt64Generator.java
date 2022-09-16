package cz.mg.vulkantransformator.services.translator.generators.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.generators.CGenerator;

public @Service class CUInt64Generator implements CGenerator {
    private static @Optional CUInt64Generator instance;

    public static @Mandatory CUInt64Generator getInstance() {
        if (instance == null) {
            instance = new CUInt64Generator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CUInt64Generator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CUInt64";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "long");
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return typeGenerator.generateNative(getName(), "jlong", "uint64_t");
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
