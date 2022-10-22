package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkConstant;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Service class VkConstantsTranslator {
    private static @Optional VkConstantsTranslator instance;

    public static @Mandatory VkConstantsTranslator getInstance() {
        if (instance == null) {
            instance = new VkConstantsTranslator();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private CodeGenerator codeGenerator;

    private VkConstantsTranslator() {
    }

    public String getName(@Mandatory LibraryConfiguration configuration) {
        return "Vk" + configuration.getSubModulePrefix() + "Constants";
    }

    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkRoot root,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = codeGenerator.generateJavaHeading(configuration);

        lines.addCollectionLast(
            new List<>(
                "public class " + getName(configuration) + " extends CObject {",
                "    private " + getName(configuration) + "(long address) {",
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

    public @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory VkRoot root,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = codeGenerator.generateNativeHeading(configuration, null);

        for (VkComponent component : root.getComponents()) {
            if (component instanceof VkConstant) {
                VkConstant constant = (VkConstant) component;
                if (isString(constant)) {
                    lines.addLast(generateNativeVariable(constant, configuration));
                    lines.addLast("");
                    lines.addCollectionLast(generateNativeFunction(constant, configuration));
                    lines.addLast("");
                }
            }
        }

        return lines;
    }

    private @Mandatory String generateNativeVariable(
        @Mandatory VkConstant constant,
        @Mandatory LibraryConfiguration configuration
    ) {
        return "const char* _" + constant.getName() + " = " + constant.getName() + ";";
    }

    private @Mandatory List<String> generateNativeFunction(
        @Mandatory VkConstant constant,
        @Mandatory LibraryConfiguration configuration
    ) {
        return codeGenerator.generateJniFunction(configuration, constantToFunction(constant, configuration));
    }

    private @Mandatory JniFunction constantToFunction(
        @Mandatory VkConstant constant,
        @Mandatory LibraryConfiguration configuration
    ) {
        JniFunction function = new JniFunction();
        function.setStatic(true);
        function.setOutput("jlong");
        function.setClassName(getName(configuration));
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
