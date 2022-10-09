package cz.mg.vulkantransformator.services;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.vulkantransformator.entities.vulkan.VkVersion;

public @Utility class Configuration {
    public static @Mandatory String JAVA_DIRECTORY = "/usr/lib/jvm/default-java/include";
    public static @Mandatory String JAVA_DIRECTORY_MD = "/usr/lib/jvm/default-java/include/linux";
    public static @Mandatory String VULKAN_FILE_NAME = "vulkan_core.h";
    public static @Mandatory VkVersion VULKAN_VERSION = new VkVersion(1, 1);
}
