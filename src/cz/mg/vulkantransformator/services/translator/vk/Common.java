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

    public @Mandatory List<String> getCommonJavaHeader(@Mandatory VkComponent component) {
        return new List<>(
            "package " + Configuration.PACKAGE + ";",
            "",
            "public class " + component.getName() + " {",
            "    private final long address;",
            "",
            "    public " + component.getName() + "(long address) {",
            "        this.address = address;",
            "    }",
            "",
            "    public long address() {",
            "        return address;",
            "    }",
            "",
            "    public long size() {",
            "        return _size();",
            "    }",
            "",
            "    private static native long _size();",
            "",
            "    public void set(" + component.getName() + " object) {",
            "        _set(object.address, address);",
            "    }",
            "",
            "    private static native void _set(long source, long destination);",
            ""
        );
    }

    public @Mandatory List<String> getCommonJavaFooter(@Mandatory VkComponent component) {
        return new List<>("}");
    }

    public @Mandatory List<String> getCommonNativeHeader(@Mandatory VkComponent component) {
        String path = Configuration.FUNCTION + "_" + component.getName();
        return new List<>(
            "#include <jni.h>",
            "#include <vulkan/vulkan.h>",
            "#include <string.h>",
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
            "JNIEXPORT jlong JNICALL Java_" + path + "__size(JNIEnv* env, jclass clazz) {",
            "    return sizeof(" + component.getName() + ");",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "__set(JNIEnv* env, jclass clazz, jlong source, jlong destination) {",
            "    memcpy(l2a(destination), l2a(source), sizeof(" + component.getName() + "));",
            "}",
            ""
        );
    }

    public @Mandatory List<String> getCommonNativeFooter(@Mandatory VkComponent component) {
        return new List<>();
    }
}
