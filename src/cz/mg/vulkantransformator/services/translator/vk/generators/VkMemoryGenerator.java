package cz.mg.vulkantransformator.services.translator.vk.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;

public @Service class VkMemoryGenerator implements VkGenerator {
    private static @Optional VkMemoryGenerator instance;

    public static @Mandatory VkMemoryGenerator getInstance() {
        if (instance == null) {
            instance = new VkMemoryGenerator();
        }
        return instance;
    }

    private VkMemoryGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "VkMemory";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        return new List<>(
            "package " + Configuration.PACKAGE + ";",
            "",
            "public class VkMemory {",
            // TODO
            "}",
            ""
        );
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return new List<>(
            "#Include \"" + getName() + "\"",
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
        );
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>(
            "#include <stdlib.h>",
            "#include <string.h>",
            "",
            "void* l2a(jlong l);",
            "jlong a2l(void* a);",
            ""
        );
    }
}
