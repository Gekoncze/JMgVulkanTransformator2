package cz.mg.vulkantransformator.services.translator.vk.wayland;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Utility class WaylandLibraryConfiguration implements LibraryConfiguration {
    private static @Optional WaylandLibraryConfiguration instance;

    public static @Mandatory WaylandLibraryConfiguration getInstance() {
        if (instance == null) {
            instance = new WaylandLibraryConfiguration();
        }
        return instance;
    }

    public WaylandLibraryConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "Wayland";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgvulkanwayland";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.vulkan.wayland";
    }
}
