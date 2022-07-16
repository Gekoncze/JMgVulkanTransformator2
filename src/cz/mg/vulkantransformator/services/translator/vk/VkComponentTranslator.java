package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.vk.generators.CMemoryGenerator;
import cz.mg.vulkantransformator.services.translator.vk.generators.CTypeGenerator;

public @Service class VkComponentTranslator {
    private static @Optional VkComponentTranslator instance;

    public static @Mandatory VkComponentTranslator getInstance() {
        if (instance == null) {
            instance = new VkComponentTranslator();
            instance.memoryGenerator = CMemoryGenerator.getInstance();
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CMemoryGenerator memoryGenerator;
    private CTypeGenerator typeGenerator;

    private VkComponentTranslator() {
    }

    public @Mandatory List<String> getCommonJavaHeader(@Mandatory VkComponent component) {
        String typeName = typeGenerator.getName();
        return new List<>(
            "package " + Configuration.PACKAGE + ";",
            "",
            "public class " + component.getName() + " extends CVoid {",
            "    public static final " + typeName + "<" + component.getName() + "> TYPE = new " + typeName + "<>(",
            "        _size(), " + component.getName() + "::new",
            "    );",
            "",
            "    public " + component.getName() + "(long address) {",
            "        super(address);",
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
        String path = getNativeComponentPath(component);
        return new List<>(
            "#include <jni.h>",
            "#include <vulkan/vulkan.h>",
            "#include \"" + memoryGenerator.getName() + ".h\"",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "_size(JNIEnv* env, jclass clazz) {",
            "    return sizeof(" + component.getName() + ");",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_set(JNIEnv* env, jclass clazz, jlong source, jlong destination) {",
            "    memcpy(l2a(destination), l2a(source), sizeof(" + component.getName() + "));",
            "}",
            ""
        );
    }

    public @Mandatory List<String> getCommonNativeFooter(@Mandatory VkComponent component) {
        return new List<>();
    }

    public @Mandatory String getNativeComponentPath(@Mandatory VkComponent component) {
        return Configuration.FUNCTION + "_" + component.getName() + "_";
    }
}
