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
            instance.typeGenerator = CTypeGenerator.getInstance();
        }
        return instance;
    }

    private CMemoryGenerator memoryGenerator;
    private CTypeGenerator typeGenerator;

    private CPointerGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CPointer";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        String typeName = typeGenerator.getName();
        String type = typeName + "<T>";
        return new List<>(
            "package " + Configuration.PACKAGE + ";",
            "",
            "public class " + getName() + "<T> {",
            "    public static final " + typeName + "<" + getName() + "> TYPE = new " + typeName + "<>(",
            "        _size(), (a) -> { throw new RuntimeException(\"Cannot create pointer.\"); }",
            "    );",
            "",
            "    private final long address;",
            "    private final " + type + " type;",
            "",
            "    public " + getName() + "(long address, " + type + " type) {",
            "        this.address = address;",
            "        this.type = type;",
            "    }",
            "",
            "    private static native long _size();",
            "",
            "    public T getTarget() {",
            "        return getTarget(0);",
            "    }",
            "",
            "    public T getTarget(int i) {",
            "        long targetAddress = get() + i * type.getSize();",
            "        if (targetAddress != CMemory.NULL) {",
            "            return type.getFactory().create(targetAddress);",
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
        String path = Configuration.FUNCTION + "_" + getName() + "_";
        return new List<>(
            "#include \"" + memoryGenerator.getName() + ".h\"",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "_size(JNIEnv* env, jclass clazz) {",
            "    return sizeof(void*);",
            "}",
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
