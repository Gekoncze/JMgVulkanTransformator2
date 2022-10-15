package cz.mg.vulkantransformator.services.translator.vk.wayland;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.ObjectCodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkGenerator;

public @Service class WlDisplayGenerator implements VkGenerator {
    private static @Optional WlDisplayGenerator instance;

    public static @Mandatory WlDisplayGenerator getInstance() {
        if (instance == null) {
            instance = new WlDisplayGenerator();
            instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
            instance.configuration = VkWaylandConfiguration.getInstance();
        }
        return instance;
    }

    private VkWaylandConfiguration configuration;
    private ObjectCodeGenerator objectCodeGenerator;

    private WlDisplayGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "wl_display";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        List<String> lines = objectCodeGenerator.getCommonJavaHeader(getName(), configuration);
        lines.addCollectionLast(objectCodeGenerator.getCommonJavaFooter());
        return lines;
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        List<String> lines = objectCodeGenerator.getCommonNativeHeader(getName(), configuration);
        lines.addCollectionLast(objectCodeGenerator.getCommonNativeFooter());
        return lines;
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
