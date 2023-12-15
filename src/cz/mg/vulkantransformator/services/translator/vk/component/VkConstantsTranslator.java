package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.*;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.vk.Index;

public @Service class VkConstantsTranslator {
    private static volatile @Service VkConstantsTranslator instance;

    public static @Mandatory VkConstantsTranslator getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkConstantsTranslator();
                    instance.codeGenerator = CodeGenerator.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service CodeGenerator codeGenerator;

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
                 if (constant instanceof VkIntegerConstant) {
                    lines.addCollectionLast(
                        new List<>(
                            "    public static final int " + constant.getName() + " = " + constant.getValue() + ";",
                            ""
                        )
                    );
                } else if (constant instanceof VkFloatConstant) {
                    lines.addCollectionLast(
                        new List<>(
                            "    public static final float " + constant.getName() + " = " + constant.getValue() + ";",
                            ""
                        )
                    );
                } else if (constant instanceof VkStringConstant) {
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
                } else {
                    throw new UnsupportedOperationException(
                        "Unsupported constant " + constant.getName()
                            + " of type " + configuration.getClass().getSimpleName()
                            + " with value '" + constant.getValue() + "'."
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
                if (constant instanceof VkStringConstant) {
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
}
