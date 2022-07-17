package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.vk.generators.CMemoryGenerator;
import cz.mg.vulkantransformator.services.translator.vk.generators.CObjectGenerator;

public @Service class VkComponentTranslator {
    private static @Optional VkComponentTranslator instance;

    public static @Mandatory VkComponentTranslator getInstance() {
        if (instance == null) {
            instance = new VkComponentTranslator();
            instance.memoryGenerator = CMemoryGenerator.getInstance();
            instance.objectGenerator = CObjectGenerator.getInstance();
        }
        return instance;
    }

    private CMemoryGenerator memoryGenerator;
    private CObjectGenerator objectGenerator;

    private VkComponentTranslator() {
    }

    public @Mandatory List<String> getCommonJavaHeader(@Mandatory VkComponent component) {
        return new List<>(
            "package " + Configuration.VULKAN_PACKAGE + ";",
            "",
            "import " + Configuration.C_PACKAGE + ".*;",
            "",
            "public class " + component.getName() + " extends " + objectGenerator.getName() + " {",
            "    public static final long SIZE = _size();",
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
        return Configuration.VULKAN_FUNCTION + "_" + component.getName() + "_";
    }
}
