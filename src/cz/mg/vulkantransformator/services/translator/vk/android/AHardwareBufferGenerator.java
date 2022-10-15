package cz.mg.vulkantransformator.services.translator.vk.android;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.ObjectCodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkGenerator;

public @Service class AHardwareBufferGenerator implements VkGenerator {
    private static @Optional AHardwareBufferGenerator instance;

    public static @Mandatory AHardwareBufferGenerator getInstance() {
        if (instance == null) {
            instance = new AHardwareBufferGenerator();
            instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
            instance.configuration = VkAndroidLibraryConfiguration.getInstance();
        }
        return instance;
    }

    private VkAndroidLibraryConfiguration configuration;
    private ObjectCodeGenerator objectCodeGenerator;

    private AHardwareBufferGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "AHardwareBuffer";
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
