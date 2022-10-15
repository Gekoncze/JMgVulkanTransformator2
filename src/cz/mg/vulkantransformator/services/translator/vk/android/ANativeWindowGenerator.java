package cz.mg.vulkantransformator.services.translator.vk.android;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.ObjectCodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkGenerator;

public @Service class ANativeWindowGenerator implements VkGenerator {
    private static @Optional ANativeWindowGenerator instance;

    public static @Mandatory ANativeWindowGenerator getInstance() {
        if (instance == null) {
            instance = new ANativeWindowGenerator();
            instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
            instance.configuration = VkAndroidLibraryConfiguration.getInstance();
        }
        return instance;
    }

    private VkAndroidLibraryConfiguration configuration;
    private ObjectCodeGenerator objectCodeGenerator;

    private ANativeWindowGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "ANativeWindow";
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
