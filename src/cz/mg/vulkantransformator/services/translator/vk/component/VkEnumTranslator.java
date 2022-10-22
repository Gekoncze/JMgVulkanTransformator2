package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkEnum;
import cz.mg.vulkantransformator.entities.vulkan.VkEnumEntry;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.ObjectCodeGenerator;

public @Service class VkEnumTranslator implements VkTranslator<VkEnum> {
    private static @Optional VkEnumTranslator instance;

    public static @Mandatory VkEnumTranslator getInstance() {
        if (instance == null) {
            instance = new VkEnumTranslator();
            instance.enumEntryTranslator = VkEnumEntryTranslator.getInstance();
            instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private VkEnumEntryTranslator enumEntryTranslator;
    private ObjectCodeGenerator objectCodeGenerator;
    private CodeGenerator codeGenerator;

    private VkEnumTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkEnum.class;
    }

    @Override
    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkEnum enumeration,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            objectCodeGenerator.getCommonJavaHeading(enumeration.getName(), configuration)
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
            objectCodeGenerator.getCommonJavaFooter()
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory VkEnum enumeration,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeHeading(enumeration.getName(), enumeration.getName(), null, configuration)
        );

        JniFunction getFunction = new JniFunction();
        getFunction.setStatic(true);
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
        setFunction.setStatic(true);
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
                enumEntryTranslator.translateNative(enumeration, entry, configuration)
            );
        }

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeFooter()
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNativeHeader(
        @Mandatory Index index,
        @Mandatory VkEnum component,
        @Mandatory LibraryConfiguration configuration
    ) {
        return new List<>();
    }
}
