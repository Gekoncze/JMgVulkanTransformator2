package cz.mg.vulkantransformator.services.translator.vk.generators.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.vk.generators.VkGenerator;

public @Service class CCharGenerator implements VkGenerator {
    private static @Optional CCharGenerator instance;

    public static @Mandatory CCharGenerator getInstance() {
        if (instance == null) {
            instance = new CCharGenerator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CCharGenerator() {
    }


    @Override
    public boolean isVulkan() {
        return false;
    }

    @Override
    public @Mandatory String getName() {
        return "char_t";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "char");
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return typeGenerator.generateNative(getName(), "jchar", "char");
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
