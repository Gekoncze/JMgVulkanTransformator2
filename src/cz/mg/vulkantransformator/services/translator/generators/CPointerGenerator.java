package cz.mg.vulkantransformator.services.translator.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class CPointerGenerator implements CGenerator {
    private static @Optional CPointerGenerator instance;

    public static @Mandatory CPointerGenerator getInstance() {
        if (instance == null) {
            instance = new CPointerGenerator();
            instance.memoryGenerator = CMemoryGenerator.getInstance();
            instance.factoryGenerator = CFactoryGenerator.getInstance();
            instance.objectGenerator = CObjectGenerator.getInstance();
        }
        return instance;
    }

    private CMemoryGenerator memoryGenerator;
    private CFactoryGenerator factoryGenerator;
    private CObjectGenerator objectGenerator;

    private CPointerGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CPointer";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        String genericFactoryName = factoryGenerator.getName() + "<T>";
        return new List<>(
            "package " + Configuration.C_PACKAGE + ";",
            "",
            "public class " + getName() + "<T> extends " + objectGenerator.getName() + " {",
            "    public static final long SIZE = _size();",
            "",
            "    private final long size;",
            "    private final " + genericFactoryName + " factory;",
            "",
            "    public " + getName() + "(long address, long size, " + genericFactoryName + " factory) {",
            "        super(address);",
            "        this.size = size;",
            "        this.factory = factory;",
            "    }",
            "",
            "    private static native long _size();",
            "",
            "    public T getTarget() {",
            "        return getTarget(0);",
            "    }",
            "",
            "    public T getTarget(int i) {",
            "        long targetAddress = get() + i * size;",
            "        if (targetAddress != CMemory.NULL) {",
            "            return factory.create(targetAddress);",
            "        } else {",
            "            throw new NullPointerException();",
            "        }",
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
            "}"
        );
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        String path = Configuration.C_FUNCTION + "_" + getName() + "_";
        return new List<>(
            "#include \"" + memoryGenerator.getName() + ".h\"",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "_size(JNIEnv* env, jclass clazz) {",
            "    return sizeof(void*);",
            "}",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "_get(JNIEnv* env, jclass clazz, jlong address) {",
            "    void** a = (void**) l2a(address);",
            "    return *a;",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_set(JNIEnv* env, jclass clazz, jlong address, jlong value) {",
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
