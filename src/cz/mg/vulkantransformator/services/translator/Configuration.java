package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;

public @Utility class Configuration {
    public static @Mandatory String PACKAGE = "cz.mg.vulkan";
    public static @Mandatory String DIRECTORY = PACKAGE.replace('.', '/');
}
