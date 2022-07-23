package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkConstant;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.generators.CMemoryGenerator;
import cz.mg.vulkantransformator.services.translator.generators.CObjectGenerator;
import cz.mg.vulkantransformator.services.translator.generators.CPointerGenerator;
import cz.mg.vulkantransformator.services.translator.generators.types.CCharGenerator;

public @Service class VkConstantTranslator {
    private static @Optional VkConstantTranslator instance;

    public static @Mandatory VkConstantTranslator getInstance() {
        if (instance == null) {
            instance = new VkConstantTranslator();
            instance.objectGenerator = CObjectGenerator.getInstance();
            instance.memoryGenerator = CMemoryGenerator.getInstance();
            instance.pointerGenerator = CPointerGenerator.getInstance();
            instance.charGenerator = CCharGenerator.getInstance();
        }
        return instance;
    }

    private CObjectGenerator objectGenerator;
    private CMemoryGenerator memoryGenerator;
    private CPointerGenerator pointerGenerator;
    private CCharGenerator charGenerator;

    private VkConstantTranslator() {
    }

    public String getName() {
        return "VkConstant";
    }

    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkRoot root) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            new List<>(
                "package " + Configuration.VULKAN_PACKAGE + ";",
                "",
                "import " + Configuration.C_PACKAGE + ".*;",
                "",
                "public class " + getName() + " extends " + objectGenerator.getName() + " {",
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
                    String charName = charGenerator.getName();
                    String pointerName = pointerGenerator.getName();
                    String type = pointerName + "<" + charName + ">";
                    lines.addCollectionLast(
                        new List<>(
                            "    public static final " + type + " " + constant.getName() + " = new "+ pointerName + "<>(",
                            "         get_" + constant.getName() + "()" + ",",
                            "         " + charName + ".SIZE,",
                            "         " + charName + "::new",
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
        List<String> lines = new List<>();

        String path = Configuration.VULKAN_FUNCTION + "_" + getName() + "_";
        lines.addCollectionLast(
            new List<>(
                "#include <vulkan/vulkan.h>",
                "#include \"" + memoryGenerator.getName() + ".h\"",
                ""
            )
        );

        for (VkComponent component : root.getComponents()) {
            if (component instanceof VkConstant) {
                VkConstant constant = (VkConstant) component;
                if (isString(constant)) {
                    lines.addCollectionLast(
                        new List<>(
                            "const char* _" + constant.getName() + " = " + constant.getName() + ";",
                            "",
                            "JNIEXPORT jlong JNICALL Java_" + path + "get_" + constant.getName() + "(JNIEnv* env, jclass clazz) {",
                            "    return a2l(_" + constant.getName() + ");",
                            "}",
                            ""
                        )
                    );
                }
            }
        }

        return lines;
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
