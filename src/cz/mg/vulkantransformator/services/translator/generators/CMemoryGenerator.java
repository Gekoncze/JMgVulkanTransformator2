package cz.mg.vulkantransformator.services.translator.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class CMemoryGenerator implements CGenerator {
    private static @Optional CMemoryGenerator instance;

    public static @Mandatory CMemoryGenerator getInstance() {
        if (instance == null) {
            instance = new CMemoryGenerator();
        }
        return instance;
    }

    private CMemoryGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CMemory";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + Configuration.C_PACKAGE + ";",
            "",
            "public class " + getName() + " implements AutoCloseable {",
            "    public static final long NULL = getNull();",
            "",
            "    private long address;",
            "",
            "    private " + getName() + "(long address) {",
            "        this.address = address;",
            "    }",
            "",
            "    public long getAddress() {",
            "        return address;",
            "    }",
            "",
            "    @Override",
            "    public void close() {",
            "        freeMemory(this);",
            "    }",
            "",
            "    private static native long getNull();",
            "",
            "    public static " + getName() + " allocateMemory(long size) {",
            "        long address = allocate(size);",
            "        if (address == NULL) {",
            "            throw new RuntimeException(\"Could not allocate memory of size \" + size + \".\");",
            "        }",
            "        return new " + getName() + "(address);",
            "    }",
            "",
            "    public static native long allocate(long size);",
            "",
            "    public static void freeMemory(" + getName() + " memory) {",
            "        if (memory.address != NULL) {",
            "            free(memory.address);",
            "            memory.address = NULL;",
            "        }",
            "    }",
            "",
            "    public static native long free(long address);",
            "}"
        );
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        String path = Configuration.C_FUNCTION + "_" + getName() + "_";
        return new List<>(
            "#include \"" + getName() + "\"",
            "",
            "void* l2a(jlong l) {",
            "    union {",
            "        jlong l;",
            "        void* a;",
            "    } c;",
            "    c.l = l;",
            "    return c.a;",
            "}",
            "",
            "jlong a2l(void* a) {",
            "    union {",
            "        jlong l;",
            "        void* a;",
            "    } c;",
            "    c.a = a;",
            "    return c.l;",
            "}",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "getNull(JNIEnv* env, jclass clazz) {",
            "    return a2l(NULL);",
            "}",
            "",
            "JNIEXPORT jlong JNICALL Java_" + path + "allocate(JNIEnv* env, jclass clazz, jlong size) {",
            "    return a2l(calloc(1, size));",
            "}",
            "",
            "JNIEXPORT void JNICALL Java_" + path + "free(JNIEnv* env, jclass clazz, jlong address) {",
            "    free(l2a(address));",
            "}"
        );
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>(
            "#include <stdlib.h>",
            "#include <string.h>",
            "#include <jni.h>",
            "",
            "void* l2a(jlong l);",
            "jlong a2l(void* a);"
        );
    }
}
