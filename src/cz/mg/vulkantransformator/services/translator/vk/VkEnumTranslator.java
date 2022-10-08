package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkEnum;
import cz.mg.vulkantransformator.entities.vulkan.VkEnumEntry;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.Index;

public @Service class VkEnumTranslator implements VkTranslator<VkEnum> {
    private static @Optional VkEnumTranslator instance;

    public static @Mandatory VkEnumTranslator getInstance() {
        if (instance == null) {
            instance = new VkEnumTranslator();
            instance.componentTranslator = VkComponentTranslator.getInstance();
            instance.enumEntryTranslator = VkEnumEntryTranslator.getInstance();
            instance.configuration = VkLibraryConfiguration.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private VkComponentTranslator componentTranslator;
    private VkEnumEntryTranslator enumEntryTranslator;
    private VkLibraryConfiguration configuration;
    private CodeGenerator codeGenerator;

    private VkEnumTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkEnum.class;
    }

    @Override
    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkEnum enumeration) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            componentTranslator.getCommonJavaHeader(enumeration)
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
            ""
        ));

        for (VkEnumEntry entry : enumeration.getEntries()) {
            lines.addCollectionLast(
                enumEntryTranslator.translateJava(enumeration, entry)
            );
        }

        codeGenerator.removeLastEmptyLine(lines);

        lines.addCollectionLast(
            componentTranslator.getCommonJavaFooter(enumeration)
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkEnum enumeration) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            componentTranslator.getCommonNativeHeader(enumeration)
        );

        JniFunction getFunction = new JniFunction();
        getFunction.setOutput("jint");
        getFunction.setClassName(enumeration.getName());
        getFunction.setName("_get2");
        getFunction.setInput(
            new List<>(
                "jlong address"
            )
        );
        getFunction.setLines(
            new List<>(
                enumeration.getName() + "* a = (" + enumeration.getName() + "*) l2a(address);",
                "return *a;"
            )
        );

        JniFunction setFunction = new JniFunction();
        setFunction.setOutput("void");
        setFunction.setClassName(enumeration.getName());
        setFunction.setName("_set2");
        setFunction.setInput(
            new List<>(
                "jlong address",
                "jint value"
            )
        );
        setFunction.setLines(
            new List<>(
                enumeration.getName() + "* a = (" + enumeration.getName() + "*) l2a(address);",
                "*a = value;"
            )
        );

        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, getFunction));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, setFunction));
        lines.addLast("");

        for (VkEnumEntry entry : enumeration.getEntries()) {
            lines.addCollectionLast(
                enumEntryTranslator.translateNative(enumeration, entry)
            );
        }

        lines.addCollectionLast(
            componentTranslator.getCommonNativeFooter(enumeration)
        );

        return lines;
    }
}
