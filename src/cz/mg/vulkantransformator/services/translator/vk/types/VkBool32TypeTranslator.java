package cz.mg.vulkantransformator.services.translator.vk.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkConstant;
import cz.mg.vulkantransformator.entities.vulkan.VkType;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.Index;

public @Service class VkBool32TypeTranslator implements VkSpecialTypeTranslator {
    private static @Optional VkBool32TypeTranslator instance;

    public static @Mandatory VkBool32TypeTranslator getInstance() {
        if (instance == null) {
            instance = new VkBool32TypeTranslator();
        }
        return instance;
    }

    private VkBool32TypeTranslator() {
    }

    @Override
    public @Mandatory String getName() {
        return "VkBool32";
    }

    @Override
    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkType type) {
        String vkTrue = ((VkConstant)index.getComponent("VK_TRUE")).getValue();
        String vkFalse = ((VkConstant)index.getComponent("VK_FALSE")).getValue();
        return new List<>(
            "    public boolean get() {",
            "        return _get(address) != " + vkFalse + ";",
            "    }",
            "",
            "    private static native int _get(long address);",
            "",
            "    public void set(boolean value) {",
            "        _set2(address, value ? " + vkTrue + " : " + vkFalse + ");",
            "    }",
            "",
            "    private static native void _set2(long address, int value);"
        );
    }

    @Override
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkType type) {
        String path = Configuration.VULKAN_FUNCTION + "_VkBool32_";
        return new List<>(
            "JNIEXPORT jint JNICALL Java_" + path + "_get(JNIEnv* env, jclass clazz, jlong address) {",
            "    VkBool32* a = (VkBool32*) l2a(address);",
            "    return *a;",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_set2(JNIEnv* env, jclass clazz, jlong address, jint value) {",
            "    VkBool32* a = (VkBool32*) l2a(address);",
            "    *a = value;",
            "}"
        );
    }
}
