package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;

public @Service class JavaConfiguration {
    private static @Optional JavaConfiguration instance;

    public static @Mandatory JavaConfiguration getInstance() {
        if (instance == null) {
            instance = new JavaConfiguration();
        }
        return instance;
    }

    private JavaConfiguration() {
    }

    public @Mandatory String getJavaDirectory() {
        return "/usr/lib/jvm/default-java/include";
    }

    public @Mandatory String getJavaDirectoryMd() {
        return "/usr/lib/jvm/default-java/include/linux";
    }
}
