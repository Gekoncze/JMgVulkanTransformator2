package cz.mg.vulkantransformator.services.translator.vk.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class CPointerGenerator implements VkGenerator {
    private static @Optional CPointerGenerator instance;

    public static @Mandatory CPointerGenerator getInstance() {
        if (instance == null) {
            instance = new CPointerGenerator();
            instance.memoryGenerator = CMemoryGenerator.getInstance();
        }
        return instance;
    }

    private CMemoryGenerator memoryGenerator;

    private CPointerGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CPointer";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + Configuration.PACKAGE + ";",
            "",
            "public class " + getName() + "<T> {",
            "    private final long address;",
            "    private final long size;",
            "    private final Factory<T> factory;",
            "",
            "    public " + getName() + "(long address, long size, Factory<T> factory) {",
            "        this.address = address;",
            "        this.size = size;",
            "        this.factory = factory;",
            "    }",
            "",
            "    public T getTarget() {",
            "        return factory.create(get());",
            "    }",
            "",
            "    public T getTarget(int i) {",
            "        return factory.create(get() + i * size);",
            "    }",
            "",
            "    public long get() {",
            "        return _get(address);",
            "    }",
            "",
            "    private static native long _get(long address);",
            "",
            "    public void set(long value) {",
            "        _set(address, value);",
            "    }",
            "",
            "    private static native void _set(long address, long value);",
            "",
            "    public interface Factory<T> {",
            "        T create(long address);",
            "    }",
            "}"
        );
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        String path = Configuration.FUNCTION + "_" + getName() + "_";
        return new List<>(
            "#include \"" + memoryGenerator.getName() + ".h\"",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "_get(JNIEnv* env, jclass clazz, long address) {",
            "    void** a = (void**) l2a(address);",
            "    return *a;",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_set(JNIEnv* env, jclass clazz, long address, long value) {",
            "    void** a = (void**) l2a(address);",
            "    *a = l2a(value);",
            "}"
        );
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
