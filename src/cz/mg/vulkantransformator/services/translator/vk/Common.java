package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class Common {
    private static @Optional Common instance;

    public static @Mandatory Common getInstance() {
        if (instance == null) {
            instance = new Common();
        }
        return instance;
    }

    private Common() {
    }

    public @Mandatory List<String> getCommonHeader(@Mandatory VkComponent component) {
        String path = Configuration.FUNCTION + "_" + component.getName();
        return new List<>(
            "#include <jni.h>",
            "#include <vulkan/vulkan.h>",
            "",
            "inline void* l2a(jlong l) {",
            "    union {",
            "        jlong l;",
            "        void* a;",
            "    } c;",
            "    c.l = l;",
            "    return c.a;",
            "}",
            "",
            "inline jlong a2l(void* a) {",
            "    union {",
            "        jlong l;",
            "        void* p;",
            "    } c;",
            "    c.a = a;",
            "    return c.l;",
            "}",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "_sizeof(JNIEnv* env, jclass clazz) {",
            "    return sizeof(" + component.getName() + ");",
            "}"
        );
    }
}
