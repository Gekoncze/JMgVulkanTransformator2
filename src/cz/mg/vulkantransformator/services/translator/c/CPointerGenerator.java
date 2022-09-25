package cz.mg.vulkantransformator.services.translator.c;

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
        String memoryName = memoryGenerator.getName();
        String objectName = objectGenerator.getName();
        return new List<>(
            "package " + Configuration.C_PACKAGE + ";",
            "",
            "public class " + getName() + "<T extends " + objectName + "> extends " + objectGenerator.getName() + " {",
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
            "        return create(get());",
            "    }",
            "",
            "    public T getTarget(int i) {",
            "        return create(offset(get(), i, size));",
            "    }",
            "",
            "    public void setTarget(T target) {",
            "        set(target == null ? " + memoryName + ".NULL : target.getAddress());",
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
            "    public static native long offset(long address, int i, long size);",
            "",
            "    private T create(long targetAddress) {",
            "        if (targetAddress != " + memoryName + ".NULL) {",
            "            return factory.create(targetAddress);",
            "        } else {",
            "            return null;",
            "        }",
            "    }",
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
            "    return a2l(*a);",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_set(JNIEnv* env, jclass clazz, jlong address, jlong value) {",
            "    void** a = (void**) l2a(address);",
            "    *a = l2a(value);",
            "}",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "offset(JNIEnv* env, jclass clazz, jlong address, jint i, jlong size) {",
            "    void* a = (void*) l2a(address);",
            "    return a2l(a + i * size);",
            "}"
        );
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
