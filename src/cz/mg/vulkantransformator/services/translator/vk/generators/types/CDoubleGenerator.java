package cz.mg.vulkantransformator.services.translator.vk.generators.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.vk.generators.VkGenerator;

public @Service class CDoubleGenerator implements VkGenerator {
    private static @Optional CDoubleGenerator instance;

    public static @Mandatory CDoubleGenerator getInstance() {
        if (instance == null) {
            instance = new CDoubleGenerator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CDoubleGenerator() {
    }


    @Override
    public boolean isVulkan() {
        return false;
    }

    @Override
    public @Mandatory String getName() {
        return "CDouble";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "double");
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return typeGenerator.generateNative(getName(), "jdouble", "double");
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
