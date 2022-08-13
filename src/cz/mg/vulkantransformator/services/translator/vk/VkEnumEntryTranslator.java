package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkEnum;
import cz.mg.vulkantransformator.entities.vulkan.VkEnumEntry;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class VkEnumEntryTranslator {
    private static @Optional VkEnumEntryTranslator instance;

    public static @Mandatory VkEnumEntryTranslator getInstance() {
        if (instance == null) {
            instance = new VkEnumEntryTranslator();
        }
        return instance;
    }

    private VkEnumEntryTranslator() {
    }

    public @Mandatory List<String> translateJava(@Mandatory VkEnum enumeration, @Mandatory VkEnumEntry entry) {
        String type = enumeration.getName();
        return new List<>(
            "    public static final " + type + " " + entry.getName() + " = new "+ type + "(",
            "         get_" + entry.getName() + "()",
            "    );",
            "",
            "    public static final int " + entry.getName() + "_I" + " = " + entry.getName() + ".get();",
            "",
            "    private static native long get_" + entry.getName() + "();",
            ""
        );
    }

    public @Mandatory List<String> translateNative(@Mandatory VkEnum enumeration, @Mandatory VkEnumEntry entry) {
        String path = Configuration.VULKAN_FUNCTION + "_" + enumeration.getName() + "_";
        return new List<>(
            enumeration.getName() + " _" + entry.getName() + " = " + entry.getName() + ";",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "get_" + entry.getName() + "(JNIEnv* env, jclass clazz) {",
            "    return a2l(&_" + entry.getName() + ");",
            "}",
            ""
        );
    }
}
