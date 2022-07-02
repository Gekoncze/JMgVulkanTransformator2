package cz.mg.vulkantransformator.services.translator.vk.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class VkPointerGenerator implements VkGenerator {
    private static @Optional VkPointerGenerator instance;

    public static @Mandatory VkPointerGenerator getInstance() {
        if (instance == null) {
            instance = new VkPointerGenerator();
            instance.memoryGenerator = VkMemoryGenerator.getInstance();
        }
        return instance;
    }

    private VkMemoryGenerator memoryGenerator;

    private VkPointerGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "VkPointer";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + Configuration.PACKAGE + ";",
            "",
            "public class VkPointer<T> {",
            "    private final long address;",
            "    private final long size;",
            "    private final Factory<T> factory;",
            "",
            "    public VkPointer(long address, long size, Factory<T> factory) {",
            "        this.address = address;",
            "        this.size = size;",
            "        this.factory = factory;",
            "    }",
            "",
            "    public T get() {",
            "        return factory.create(getValue());",
            "    }",
            "",
            "    public T get(int i) {",
            "        return factory.create(getValue() + i * size);",
            "    }",
            "",
            "    public long getValue() {",
            "        return _getValue(address);",
            "    }",
            "",
            "    private static native long _getValue(long address);",
            "",
            "    public void setValue(long value) {",
            "        _setValue(address, value);",
            "    }",
            "",
            "    private static native void _setValue(long address, long value);",
            "",
            "    public interface Factory<T> {",
            "        T create(long address);",
            "    }",
            "}",
            ""
        );
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        String path = Configuration.FUNCTION + "_" + getName() + "_";
        return new List<>(
            "#include \"" + memoryGenerator.getName() + ".h\"",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "_getValue(JNIEnv* env, jclass clazz, long address) {",
            "    void** a = (void**) l2a(address);",
            "    return *a;",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_setValue(JNIEnv* env, jclass clazz, long address, long value) {",
            "    void** a = (void**) l2a(address);",
            "    *a = l2a(value);",
            "}",
            ""
        );
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
