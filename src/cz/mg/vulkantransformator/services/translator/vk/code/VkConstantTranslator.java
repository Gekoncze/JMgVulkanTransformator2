package cz.mg.vulkantransformator.services.translator.vk.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkConstant;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.vk.VkLibraryConfiguration;

public @Service class VkConstantTranslator {
    private static @Optional VkConstantTranslator instance;

    public static @Mandatory VkConstantTranslator getInstance() {
        if (instance == null) {
            instance = new VkConstantTranslator();
            instance.configuration = VkLibraryConfiguration.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private VkLibraryConfiguration configuration;
    private CodeGenerator codeGenerator;

    private VkConstantTranslator() {
    }

    public String getName() {
        return "VkConstant";
    }

    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkRoot root) {
        List<String> lines = codeGenerator.generateJavaHeader(configuration);

        lines.addCollectionLast(
            new List<>(
                "public class " + getName() + " extends CObject {",
                "    private " + getName() + "(long address) {",
                "        super(address);",
                "    }",
                ""
            )
        );

        for (VkComponent component : root.getComponents()) {
            if (component instanceof VkConstant) {
                VkConstant constant = (VkConstant) component;
                if (isInteger(constant)) {
                    lines.addCollectionLast(
                        new List<>(
                            "    public static final int " + constant.getName() + " = " + constant.getValue() + ";",
                            ""
                        )
                    );
                } else if (isFloat(constant)) {
                    lines.addCollectionLast(
                        new List<>(
                            "    public static final float " + constant.getName() + " = " + constant.getValue() + ";",
                            ""
                        )
                    );
                } else if (isString(constant)) {
                    String type = "CPointer<CChar>";
                    lines.addCollectionLast(
                        new List<>(
                            "    public static final " + type + " " + constant.getName() + " = new CPointer<>(",
                            "         get_" + constant.getName() + "()" + ",",
                            "         CChar.SIZE,",
                            "         CChar::new",
                            "    );",
                            "",
                            "    private static native long get_" + constant.getName() + "();",
                            ""
                        )
                    );
                }
            }
        }

        lines.addLast("}");

        return lines;
    }

    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkRoot root) {
        List<String> lines = codeGenerator.generateNativeHeader(configuration);

        for (VkComponent component : root.getComponents()) {
            if (component instanceof VkConstant) {
                VkConstant constant = (VkConstant) component;
                if (isString(constant)) {
                    lines.addLast(generateNativeVariable(constant));
                    lines.addLast("");
                    lines.addCollectionLast(generateNativeFunction(constant));
                    lines.addLast("");
                }
            }
        }

        return lines;
    }

    private @Mandatory String generateNativeVariable(@Mandatory VkConstant constant) {
        return "const char* _" + constant.getName() + " = " + constant.getName() + ";";
    }

    private @Mandatory List<String> generateNativeFunction(@Mandatory VkConstant constant) {
        return codeGenerator.generateJniFunction(configuration, constantToFunction(constant));
    }

    private @Mandatory JniFunction constantToFunction(@Mandatory VkConstant constant) {
        JniFunction function = new JniFunction();
        function.setOutput("jlong");
        function.setClassName(getName());
        function.setName("get_" + constant.getName());
        function.setLines(
            new List<>(
                "return a2l(&_" + constant.getName() + ");"
            )
        );
        return function;
    }

    private boolean isInteger(@Mandatory VkConstant constant)
    {
        try {
            Integer.parseInt(constant.getValue());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isFloat(@Mandatory VkConstant constant)
    {
        try {
            Float.parseFloat(constant.getValue());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isString(@Mandatory VkConstant constant)
    {
        String value = constant.getValue().trim();
        return value.startsWith("\"") || value.endsWith("\"");
    }
}
