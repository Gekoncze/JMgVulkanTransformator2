package cz.mg.vulkantransformator.services.translator.c;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public class CValidatorGenerator implements CGenerator {
    private static @Optional CValidatorGenerator instance;

    public static @Mandatory CValidatorGenerator getInstance() {
        if (instance == null) {
            instance = new CValidatorGenerator();
        }
        return instance;
    }

    private CValidatorGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CValidator";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + Configuration.C_PACKAGE + ";",
            "",
            "public class " + getName() + " {",
            "    public static void validate() {",
            "        int code = _validate();",
            "        if (code != 0) {",
            "            throw new IllegalStateException(\"Unsupported platform. Error code \" + code + \".\");",
            "        }",
            "    }",
            "",
            "    private static native int _validate();",
            "}"
        );
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        String path = Configuration.C_FUNCTION + "_" + getName() + "_";
        return new List<>(
            "#include <stdint.h>",
            "#include <jni.h>",
            "",
            "enum _ValidationEnum0001 {",
            "    one",
            "};",
            "",
            "JNIEXPORT jint JNICALL Java_" + path + "_validate(JNIEnv* env, jclass clazz) {",
            "    if (sizeof(char) != 1) return 1;",
            "    if (sizeof(byte) != 1) return 2;",
            "    if (sizeof(jbyte) != 1) return 3;",
            "    if (sizeof(enum _ValidationEnum0001) != 4) return 4;",
            "    if (sizeof(size_t) != 8) return 5;",
            "    if (sizeof(jlong) != 8) return 6;",
            "    if (sizeof(void*) != 8) return 7;",
            "    return 0;",
            "}"
        );
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
