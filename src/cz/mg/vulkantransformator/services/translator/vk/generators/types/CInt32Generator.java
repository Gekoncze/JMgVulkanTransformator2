package cz.mg.vulkantransformator.services.translator.vk.generators.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.vk.generators.VkGenerator;

public @Service class CInt32Generator implements VkGenerator {
    private static @Optional CInt32Generator instance;

    public static @Mandatory CInt32Generator getInstance() {
        if (instance == null) {
            instance = new CInt32Generator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CInt32Generator() {
    }


    @Override
    public boolean isVulkan() {
        return false;
    }

    @Override
    public @Mandatory String getName() {
        return "CInt32";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "int");
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return typeGenerator.generateNative(getName(), "jint", "int32_t");
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
