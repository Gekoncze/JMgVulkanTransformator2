package cz.mg.vulkantransformator.services.translator.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.generators.types.CCharGenerator;

public @Service class CStringGenerator implements Generator {
    private static @Optional CStringGenerator instance;

    public static @Mandatory CStringGenerator getInstance() {
        if (instance == null) {
            instance = new CStringGenerator();
            instance.pointerGenerator = CPointerGenerator.getInstance();
            instance.charGenerator = CCharGenerator.getInstance();
        }
        return instance;
    }

    private CPointerGenerator pointerGenerator;
    private CCharGenerator charGenerator;

    private CStringGenerator() {
    }

    @Override
    public boolean isVulkan() {
        return false;
    }

    @Override
    public @Mandatory String getName() {
        return "CString";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        String charPointerType = pointerGenerator.getName() + "<" + charGenerator.getName() + ">";
        return new List<>(
            "package " + Configuration.C_PACKAGE + ";",
            "",
            "public class " + getName() + " {",
            "    public static String get(" + charPointerType + " pointer) {",
            "        StringBuilder value = new StringBuilder();",
            "        int i = -1;",
            "        while (true) {",
            "            i++;",
            "            byte ch = pointer.getTarget(i).get();",
            "            if (ch != 0) {",
            "                value.append((char)ch);",
            "            } else {",
            "                break;",
            "            }",
            "        }",
            "        return value.toString();",
            "    }",
            "",
            "    public static void set(" + charPointerType + " pointer, String value) {",
            "        for (int i = 0; i < value.length(); i++) {",
            "            char ch = value.charAt(i);",
            "            if (ch > 0 && ch < 128) {",
            "                pointer.getTarget(i).set((byte)ch);",
            "            } else {",
            "                throw new UnsupportedOperationException(\"Unsupported character '\" + ch + \"'.\");",
            "            }",
            "        }",
            "        pointer.getTarget(value.length()).set((byte)0);",
            "    }",
            "}"
        );
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        return new List<>();
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
