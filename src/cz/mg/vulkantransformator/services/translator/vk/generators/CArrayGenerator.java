package cz.mg.vulkantransformator.services.translator.vk.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;

public @Service class CArrayGenerator implements VkGenerator {
    private static @Optional CArrayGenerator instance;

    public static @Mandatory CArrayGenerator getInstance() {
        if (instance == null) {
            instance = new CArrayGenerator();
        }
        return instance;
    }

    private CArrayGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CArray";
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
