package cz.mg.vulkantransformator.services.translator.vk.generators.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.vk.generators.VkGenerator;

public @Service class CInt64Generator implements VkGenerator {
    private static @Optional CInt64Generator instance;

    public static @Mandatory CInt64Generator getInstance() {
        if (instance == null) {
            instance = new CInt64Generator();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CTypeGenerator typeGenerator;

    private CInt64Generator() {
    }


    @Override
    public boolean isVulkan() {
        return false;
    }

    @Override
    public @Mandatory String getName() {
        return "int64_t";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return typeGenerator.generateJava(getName(), "long");
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return typeGenerator.generateNative(getName(), "jlong", getName());
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
