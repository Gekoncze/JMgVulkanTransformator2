package cz.mg.vulkantransformator.services.translator.generators.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.generators.Generator;

public @Service class CUInt16Generator implements Generator {
    private static @Optional CUInt16Generator instance;

    public static @Mandatory CUInt16Generator getInstance() {
        if (instance == null) {
            instance = new CUInt16Generator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CUInt16Generator() {
    }


    @Override
    public boolean isVulkan() {
        return false;
    }

    @Override
    public @Mandatory String getName() {
        return "CUInt16";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "short");
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return typeGenerator.generateNative(getName(), "jshort", "uint16_t");
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
