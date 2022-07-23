package cz.mg.vulkantransformator.services.translator.vk.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkType;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.Index;

public @Service class VkDeviceSizeTypeTranslator implements VkSpecialTypeTranslator {
    private static @Optional VkDeviceSizeTypeTranslator instance;

    public static @Mandatory VkDeviceSizeTypeTranslator getInstance() {
        if (instance == null) {
            instance = new VkDeviceSizeTypeTranslator();
        }
        return instance;
    }

    private VkDeviceSizeTypeTranslator() {
    }

    @Override
    public @Mandatory String getName() {
        return "VkDeviceSize";
    }

    @Override
    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkType type) {
        return new List<>(
            "    public long get() {",
            "        return _get(address);",
            "    }",
            "",
            "    private static native long _get(long address);",
            "",
            "    public void set(long value) {",
            "        _set2(address, value);",
            "    }",
            "",
            "    private static native void _set2(long address, long value);"
        );
    }

    @Override
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkType type) {
        String path = Configuration.VULKAN_FUNCTION + "_VkDeviceSize_";
        return new List<>(
            "JNIEXPORT jlong JNICALL Java_" + path + "_get(JNIEnv* env, jclass clazz, jlong address) {",
            "    VkDeviceSize* a = (VkDeviceSize*) l2a(address);",
            "    return *a;",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_set2(JNIEnv* env, jclass clazz, jlong address, jlong value) {",
            "    VkDeviceSize* a = (VkDeviceSize*) l2a(address);",
            "    *a = value;",
            "}"
        );
    }
}
