package cz.mg.vulkantransformator.services.translator.c.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.c.CConfiguration;

public @Service class CMemoryGenerator implements CGenerator {
    private static @Optional CMemoryGenerator instance;

    public static @Mandatory CMemoryGenerator getInstance() {
        if (instance == null) {
            instance = new CMemoryGenerator();
            instance.configuration = CConfiguration.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private CConfiguration configuration;
    private CodeGenerator codeGenerator;

    private CMemoryGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CMemory";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + configuration.getJavaPackage() + ";",
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
    public @Mandatory List<String> generateNative() {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            new List<>(
                "#include \"" + getName() + ".h\"",
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
                ""
            )
        );

        JniFunction nullFunction = new JniFunction();
        nullFunction.setStatic(true);
        nullFunction.setOutput("jlong");
        nullFunction.setClassName(getName());
        nullFunction.setName("getNull");
        nullFunction.setLines(
            new List<>(
                "return a2l(NULL);"
            )
        );

        JniFunction allocateFunction = new JniFunction();
        allocateFunction.setStatic(true);
        allocateFunction.setOutput("jlong");
        allocateFunction.setClassName(getName());
        allocateFunction.setName("allocate");
        allocateFunction.setInput(
            new List<>(
                "jlong size"
            )
        );
        allocateFunction.setLines(
            new List<>(
                "return a2l(calloc(1, size));"
            )
        );

        JniFunction freeFunction = new JniFunction();
        freeFunction.setStatic(true);
        freeFunction.setOutput("void");
        freeFunction.setClassName(getName());
        freeFunction.setName("free");
        freeFunction.setInput(
            new List<>(
                "jlong address"
            )
        );
        freeFunction.setLines(
            new List<>(
                "free(l2a(address));"
            )
        );

        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, nullFunction));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, allocateFunction));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, freeFunction));

        return lines;
    }

    @Override
    public @Mandatory List<String> generateNativeHeader() {
        return codeGenerator.addHeaderFileGuards(
            new List<>(
                "#include <stdlib.h>",
                "#include <string.h>",
                "#include <jni.h>",
                "",
                "void* l2a(jlong l);",
                "jlong a2l(void* a);"
            ),
            "JMGC_MEMORY_H"
        );
    }
}
