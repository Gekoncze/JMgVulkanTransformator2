package cz.mg.vulkantransformator.services.translator.vk.code.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkConstant;
import cz.mg.vulkantransformator.entities.vulkan.VkType;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Service class VkBool32TypeTranslator implements VkSpecialTypeTranslator {
    private static @Optional VkBool32TypeTranslator instance;

    public static @Mandatory VkBool32TypeTranslator getInstance() {
        if (instance == null) {
            instance = new VkBool32TypeTranslator();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private CodeGenerator codeGenerator;

    private VkBool32TypeTranslator() {
    }

    @Override
    public @Mandatory String getName() {
        return "VkBool32";
    }

    @Override
    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkType type,
        @Mandatory LibraryConfiguration configuration
    ) {
        String vkTrue = ((VkConstant)index.getComponents().get("VK_TRUE")).getValue();
        String vkFalse = ((VkConstant)index.getComponents().get("VK_FALSE")).getValue();
        return new List<>(
            "    public boolean get() {",
            "        return _get(address) != " + vkFalse + ";",
            "    }",
            "",
            "    private static native int _get(long address);",
            "",
            "    public void set(boolean value) {",
            "        _set2(address, value ? " + vkTrue + " : " + vkFalse + ");",
            "    }",
            "",
            "    private static native void _set2(long address, int value);"
        );
    }

    @Override
    public @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory VkType type,
        @Mandatory LibraryConfiguration configuration
    ) {
        JniFunction getFunction = new JniFunction();
        getFunction.setOutput("jint");
        getFunction.setClassName(getName());
        getFunction.setName("_get");
        getFunction.setInput(
            new List<>(
                "jlong address"
            )
        );
        getFunction.setLines(
            new List<>(
                "VkBool32* a = (VkBool32*) l2a(address);",
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
                "jint value"
            )
        );
        setFunction.setLines(
            new List<>(
                "VkBool32* a = (VkBool32*) l2a(address);",
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
