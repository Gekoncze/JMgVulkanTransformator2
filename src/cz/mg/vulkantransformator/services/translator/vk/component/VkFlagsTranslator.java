package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkFlags;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.ObjectCodeGenerator;

public @Service class VkFlagsTranslator implements VkTranslator<VkFlags> {
    private static @Optional VkFlagsTranslator instance;

    public static @Mandatory VkFlagsTranslator getInstance() {
        if (instance == null) {
            instance = new VkFlagsTranslator();
            instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private ObjectCodeGenerator objectCodeGenerator;
    private CodeGenerator codeGenerator;

    private VkFlagsTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkFlags.class;
    }

    @Override
    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkFlags flags,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            objectCodeGenerator.getCommonJavaHeading(flags.getName(), configuration)
        );

        lines.addCollectionLast(new List<>(
            "    public int get() {",
            "        return _get2(address);",
            "    }",
            "",
            "    private native int _get2(long address);",
            "",
            "    public void set(int flag) {",
            "        _set2(address, flag);",
            "    }",
            "",
            "    private native void _set2(long address, int value);",
            "",
            "    public void add(int flag) {",
            "        _add2(address, flag);",
            "    }",
            "",
            "    private static native void _add2(long address, int flag);",
            "",
            "    public void remove(int flag) {",
            "        _remove2(address, flag);",
            "    }",
            "",
            "    private static native void _remove2(long address, int flag);"
        ));

        String flagBitsName = getFlagBitsName(index, flags);
        if (flagBitsName != null) {
            lines.addCollectionLast(new List<>(
                "    public void add(" + flagBitsName + " flag) {",
                "        _add(address, flag.getAddress());",
                "    }",
                "",
                "    private static native void _add(long address, long flagAddress);",
                "",
                "    public void remove(" + flagBitsName + " flag) {",
                "        _remove(address, flag.getAddress());",
                "    }",
                "",
                "    private static native void _remove(long address, long flagAddress);"
            ));
        }

        codeGenerator.removeLastEmptyLine(lines);

        lines.addCollectionLast(
            objectCodeGenerator.getCommonJavaFooter()
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory VkFlags flags,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeHeading(flags.getName(), flags.getName(), null, configuration)
        );

        String name = flags.getName();

        JniFunction get2Function = new JniFunction();
        get2Function.setStatic(true);
        get2Function.setOutput("jint");
        get2Function.setClassName(flags.getName());
        get2Function.setName("_get2");
        get2Function.setInput(
            new List<>(
                "jlong address"
            )
        );
        get2Function.setLines(
            new List<>(
                name + "* a = (" + name + "*) l2a(address);",
                "return *a;"
            )
        );

        JniFunction set2Function = new JniFunction();
        set2Function.setStatic(true);
        set2Function.setOutput("void");
        set2Function.setClassName(flags.getName());
        set2Function.setName("_set2");
        set2Function.setInput(
            new List<>(
                "jlong address",
                "jint value"
            )
        );
        set2Function.setLines(
            new List<>(
                name + "* a = (" + name + "*) l2a(address);",
                "*a = value;"
            )
        );

        JniFunction add2Function = new JniFunction();
        add2Function.setStatic(true);
        add2Function.setOutput("void");
        add2Function.setClassName(flags.getName());
        add2Function.setName("_add2");
        add2Function.setInput(
            new List<>(
                "jlong address",
                "jint value"
            )
        );
        add2Function.setLines(
            new List<>(
                name + "* a = (" + name + "*) l2a(address);",
                "*a |= value;"
            )
        );

        JniFunction remove2Function = new JniFunction();
        remove2Function.setStatic(true);
        remove2Function.setOutput("void");
        remove2Function.setClassName(flags.getName());
        remove2Function.setName("_remove2");
        remove2Function.setInput(
            new List<>(
                "jlong address",
                "jint value"
            )
        );
        remove2Function.setLines(
            new List<>(
                name + "* a = (" + name + "*) l2a(address);",
                "*a &= ~value;"
            )
        );

        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, get2Function));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, set2Function));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, add2Function));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, remove2Function));
        lines.addLast("");

        String flagBitsName = getFlagBitsName(index, flags);
        if (flagBitsName != null) {
            JniFunction addFunction = new JniFunction();
            addFunction.setStatic(true);
            addFunction.setOutput("void");
            addFunction.setClassName(flags.getName());
            addFunction.setName("_add");
            addFunction.setInput(
                new List<>(
                    "jlong address",
                    "jlong flagAddress"
                )
            );
            addFunction.setLines(
                new List<>(
                    name + "* a = (" + name + "*) l2a(address);",
                    flagBitsName + "* b = (" + flagBitsName + "*) l2a(flagAddress);",
                    "*a |= *b;"
                )
            );

            JniFunction removeFunction = new JniFunction();
            removeFunction.setStatic(true);
            removeFunction.setOutput("void");
            removeFunction.setClassName(flags.getName());
            removeFunction.setName("_remove");
            removeFunction.setInput(
                new List<>(
                    "jlong address",
                    "jlong flagAddress"
                )
            );
            removeFunction.setLines(
                new List<>(
                    name + "* a = (" + name + "*) l2a(address);",
                    flagBitsName + "* b = (" + flagBitsName + "*) l2a(flagAddress);",
                    "*a &= ~(*b);"
                )
            );

            lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, addFunction));
            lines.addLast("");
            lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, removeFunction));
        }

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeFooter()
        );

        return lines;
    }

    private @Optional String getFlagBitsName(@Mandatory Index index, @Mandatory VkFlags flags)
    {
        String targetName = flags.getName().replace("Flags", "FlagBits");
        Object target = index.getComponents().getOptional(targetName);
        return target != null ? targetName : null;
    }

    @Override
    public @Mandatory List<String> translateNativeHeader(
        @Mandatory Index index,
        @Mandatory VkFlags component,
        @Mandatory LibraryConfiguration configuration
    ) {
        return new List<>();
    }
}
