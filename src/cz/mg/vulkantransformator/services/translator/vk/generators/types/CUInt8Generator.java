package cz.mg.vulkantransformator.services.translator.vk.generators.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.vk.generators.VkGenerator;

public @Service class CUInt8Generator implements VkGenerator {
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
        return "uint8_t";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "byte");
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return typeGenerator.generateNative(getName(), "jbyte", getName());
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}