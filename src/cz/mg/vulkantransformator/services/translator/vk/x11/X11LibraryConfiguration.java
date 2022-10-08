package cz.mg.vulkantransformator.services.translator.vk.x11;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Utility class X11LibraryConfiguration implements LibraryConfiguration {
    private static @Optional X11LibraryConfiguration instance;

    public static @Mandatory X11LibraryConfiguration getInstance() {
        if (instance == null) {
            instance = new X11LibraryConfiguration();
        }
        return instance;
    }

    public X11LibraryConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "X11";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgvulkanx11";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan.x11";
    }
}
