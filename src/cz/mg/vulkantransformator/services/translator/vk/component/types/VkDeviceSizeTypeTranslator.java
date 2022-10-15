package cz.mg.vulkantransformator.services.translator.vk.component.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkType;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Service class VkDeviceSizeTypeTranslator implements VkSpecialTypeTranslator {
    private static @Optional VkDeviceSizeTypeTranslator instance;

    public static @Mandatory VkDeviceSizeTypeTranslator getInstance() {
        if (instance == null) {
            instance = new VkDeviceSizeTypeTranslator();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private CodeGenerator codeGenerator;

    private VkDeviceSizeTypeTranslator() {
    }

    @Override
    public @Mandatory String getName() {
        return "VkDeviceSize";
    }

    @Override
    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkType type,
        @Mandatory LibraryConfiguration configuration
    ) {
        return new List<>(
            "    public long get() {",
            "        return _get(address);",
            "    }",
            "",
            "    private static native long _get(long address);",
            "",
            "    public void set(long value) {",
            "        _set2(address, value);",
            "    }",
            "",
            "    private static native void _set2(long address, long value);"
        );
    }

    @Override
    public @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory VkType type,
        @Mandatory LibraryConfiguration configuration
    ) {
        JniFunction getFunction = new JniFunction();
        getFunction.setOutput("jlong");
        getFunction.setClassName(getName());
        getFunction.setName("_get");
        getFunction.setInput(
            new List<>(
                "jlong address"
            )
        );
        getFunction.setLines(
            new List<>(
                "VkDeviceSize* a = (VkDeviceSize*) l2a(address);",
                "return *a;"
            )
        );

        JniFunction setFunction = new JniFunction();
        setFunction.setOutput("void");
        setFunction.setClassName(getName());
        setFunction.setName("_set2");
        setFunction.setInput(
            new List<>(
                "jlong address",
                "jlong value"
            )
        );
        setFunction.setLines(
            new List<>(
                "VkDeviceSize* a = (VkDeviceSize*) l2a(address);",
                "*a = value;"
            )
        );

        List<String> lines = new List<>();
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, getFunction));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, setFunction));
        return lines;
    }
}
