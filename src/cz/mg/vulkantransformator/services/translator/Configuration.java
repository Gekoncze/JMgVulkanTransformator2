package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;

public @Utility class Configuration {
    public static @Mandatory String VULKAN_PACKAGE = "cz.mg.vulkan";
    public static @Mandatory String VULKAN_DIRECTORY = VULKAN_PACKAGE.replace('.', '/');
    public static @Mandatory String VULKAN_FUNCTION = VULKAN_PACKAGE.replace('.', '_');

    public static @Mandatory String C_PACKAGE = "cz.mg.c";
    public static @Mandatory String C_DIRECTORY = C_PACKAGE.replace('.', '/');
    public static @Mandatory String C_FUNCTION = C_PACKAGE.replace('.', '_');
}
