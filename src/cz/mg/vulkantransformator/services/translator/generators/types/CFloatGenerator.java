package cz.mg.vulkantransformator.services.translator.generators.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.generators.Generator;

public @Service class CFloatGenerator implements Generator {
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
    public boolean isVulkan() {
        return false;
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
    public @Mandatory List<String> generateNativeC() {
        return typeGenerator.generateNative(getName(), "jfloat", "float");
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
