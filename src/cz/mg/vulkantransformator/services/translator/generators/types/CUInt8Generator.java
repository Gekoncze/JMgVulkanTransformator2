package cz.mg.vulkantransformator.services.translator.generators.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.generators.Generator;

public @Service class CUInt8Generator implements Generator {
    private static @Optional CUInt8Generator instance;

    public static @Mandatory CUInt8Generator getInstance() {
        if (instance == null) {
            instance = new CUInt8Generator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CUInt8Generator() {
    }


    @Override
    public boolean isVulkan() {
        return false;
    }

    @Override
    public @Mandatory String getName() {
        return "CUInt8";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "byte");
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return typeGenerator.generateNative(getName(), "jbyte", "uint8_t");
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
