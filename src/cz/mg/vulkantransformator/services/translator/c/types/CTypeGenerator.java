package cz.mg.vulkantransformator.services.translator.c.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.c.CMemoryGenerator;

public @Service class CTypeGenerator {
    private static @Optional CTypeGenerator instance;

    public static @Mandatory CTypeGenerator getInstance() {
        if (instance == null) {
            instance = new CTypeGenerator();
            instance.memoryGenerator = CMemoryGenerator.getInstance();
        }
        return instance;
    }

    private CMemoryGenerator memoryGenerator;

    private CTypeGenerator() {
    }

    public @Mandatory List<String> generateJava(@Mandatory String className, @Mandatory String javaType) {
        return new List<>(
            "package " + Configuration.C_PACKAGE + ";",
            "",
            "public class " + className + " extends CObject {",
            "    public static final long SIZE = _size();",
            "",
            "    public " + className + "(long address) {",
            "        super(address);",
            "    }",
            "",
            "    private static native long _size();",
            "",
            "    public " + javaType + " get() {",
            "        return _get(address);",
            "    }",
            "",
            "    private static native " + javaType + " _get(long address);",
            "",
            "    public void set(" + javaType + " value) {",
            "        _set(address, value);",
            "    }",
            "",
            "    private static native void _set(long address, " + javaType + " value);",
            "",
            "    public void set(" + className + " value) {",
            "        _set(address, value.get());",
            "    }",
            "}"
        );
    }

    public @Mandatory List<String> generateNative(
        @Mandatory String className,
        @Mandatory String jniType,
        @Mandatory String nativeType
    ) {
        String path = Configuration.C_FUNCTION + "_" + className + "_";
        return new List<>(
            "#include \"" + memoryGenerator.getName() + ".h\"",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "_size(JNIEnv* env, jclass clazz) {",
            "    return sizeof(" + nativeType + ");",
            "}",
            "",
            "JNIEXPORT " + jniType + " JNICALL Java_" + path + "_get(JNIEnv* env, jclass clazz, jlong address) {",
            "    " + nativeType + "* a = (" + nativeType + "*) l2a(address);",
            "    return *a;",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "_set(JNIEnv* env, jclass clazz, jlong address, " + jniType + " value) {",
            "    " + nativeType + "* a = (" + nativeType + "*) l2a(address);",
            "    *a = value;",
            "}"
        );
    }
}
