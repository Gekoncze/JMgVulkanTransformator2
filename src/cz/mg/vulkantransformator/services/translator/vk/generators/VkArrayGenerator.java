package cz.mg.vulkantransformator.services.translator.vk.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;

public @Service class VkArrayGenerator implements VkGenerator {
    private static @Optional VkArrayGenerator instance;

    public static @Mandatory VkArrayGenerator getInstance() {
        if (instance == null) {
            instance = new VkArrayGenerator();
        }
        return instance;
    }

    private VkArrayGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "VkArray";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            // TODO
        );
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return new List<>(
            // TODO
        );
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>(
            // TODO
        );
    }
}
