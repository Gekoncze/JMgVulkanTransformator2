package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkVariable;
import cz.mg.vulkantransformator.services.translator.generators.CArrayGenerator;
import cz.mg.vulkantransformator.services.translator.generators.CPointerGenerator;
import cz.mg.vulkantransformator.services.translator.generators.CObjectGenerator;

public @Service class VkFieldTranslator {
    private static @Optional VkFieldTranslator instance;

    public static @Mandatory VkFieldTranslator getInstance() {
        if (instance == null) {
            instance = new VkFieldTranslator();
            instance.vkComponentTranslator = VkComponentTranslator.getInstance();
            instance.pointerGenerator = CPointerGenerator.getInstance();
            instance.arrayGenerator = CArrayGenerator.getInstance();
            instance.objectGenerator = CObjectGenerator.getInstance();
        }
        return instance;
    }

    private static final Map<String, String> nameMap = new Map<>(
        50,
        new List<>(
            new Pair<>("void", "CObject"),
            new Pair<>("char", "CChar"),
            new Pair<>("uint8_t", "CUInt8"),
            new Pair<>("uint16_t", "CUInt16"),
            new Pair<>("uint32_t", "CUInt32"),
            new Pair<>("uint64_t", "CUInt64"),
            new Pair<>("int8_t", "CInt8"),
            new Pair<>("int16_t", "CInt16"),
            new Pair<>("int32_t", "CInt32"),
            new Pair<>("int64_t", "CInt64"),
            new Pair<>("float", "CFloat"),
            new Pair<>("double", "CDouble")
        )
    );

    private VkComponentTranslator vkComponentTranslator;
    private CPointerGenerator pointerGenerator;
    private CArrayGenerator arrayGenerator;
    private CObjectGenerator objectGenerator;

    private VkFieldTranslator() {
    }

    public @Mandatory List<String> translateJava(@Mandatory VkComponent component, @Mandatory VkVariable field) {
        String offsetFieldName = getOffsetFieldName(field);
        String offsetMethodName = getOffsetMethodName(field);
        List<String> lines = new List<>();
        lines.addLast("    private static final long " + offsetFieldName + " = " + offsetMethodName + "();");
        lines.addLast("");
        lines.addCollectionLast(translateJavaGetter(component, transformField(field)));
        lines.addLast("");
        lines.addLast("    private static native long " + offsetMethodName + "();");
        return lines;
    }

    private @Mandatory List<String> translateJavaGetter(@Mandatory VkComponent component, @Mandatory VkVariable field) {
        if (field.getArray() > 0 && field.getPointers() > 0) {
            throw new UnsupportedOperationException(
                "Unsupported pointer and array options for '" + getFullName(component, field) + "': "
                    + field.getPointers() + ", " + field.getArray() + "."
            );
        }

        if (field.getTypename().equals("void") && field.getPointers() < 1) {
            throw new IllegalArgumentException(
                "Invalid void field for '" + getFullName(component, field) + "'."
            );
        }

        if (field.getPointers() == 0 && field.getArray() == 0) {
            return translateJavaGetterValue(component, field);
        } else if (field.getPointers() > 0) {
            return translateJavaGetterPointer(component, field);
        } else if (field.getArray() > 0) {
            return translateJavaGetterArray(component, field);
        } else {
            throw new UnsupportedOperationException(
                "Unsupported pointer and array options for '" + getFullName(component, field) + "': "
                    + field.getPointers() + ", " + field.getArray() + "."
            );
        }
    }

    private @Mandatory List<String> translateJavaGetterValue(@Mandatory VkComponent component, @Mandatory VkVariable field) {
        String type = field.getTypename();
        String methodName = getMethodName(field);
        String offsetFieldName = getOffsetFieldName(field);
        return new List<>(
            "    public " + type + " " + methodName + "() {",
            "        return new " + type + "(address + " + offsetFieldName + ");",
            "    }"
        );
    }

    private @Mandatory List<String> translateJavaGetterPointer(@Mandatory VkComponent component, @Mandatory VkVariable field) {
        if (field.getPointers() == 1) {
            return translateJavaGetterPointer1D(component, field);
        } else if (field.getPointers() == 2) {
            return translateJavaGetterPointer2D(component, field);
        } else {
            String target = component.getName() + "." + field.getName();
            throw new UnsupportedOperationException(
                "Unsupported pointer count for '" + target + "': " + field.getPointers() + "."
            );
        }
    }

    private @Mandatory List<String> translateJavaGetterPointer1D(@Mandatory VkComponent component, @Mandatory VkVariable field) {
        String pointerName = pointerGenerator.getName();
        String type = pointerName + "<" + field.getTypename() + ">";
        return new List<>(
            "    public " + type + " " + getMethodName(field) + "() {",
            "        return new " + pointerName + "<>(",
            "             " + getAddressArgument(field) + ",",
            "             " + field.getTypename() + ".SIZE,",
            "             " + field.getTypename() + "::new",
            "        );",
            "    }"
        );
    }

    private @Mandatory List<String> translateJavaGetterPointer2D(@Mandatory VkComponent component, @Mandatory VkVariable field) {
        String pointerName = pointerGenerator.getName();
        String type = pointerName + "<" + pointerName + "<" + field.getTypename() + ">>";
        return new List<>(
            "    public " + type + " " + getMethodName(field) + "() {",
            "        return new " + pointerName + "<>(",
            "             " + getAddressArgument(field) + ",",
            "             " + pointerName + ".SIZE,",
            "             (a) -> new " + pointerName + "<>(",
            "                 a,",
            "                 " + field.getTypename() + ".SIZE,",
            "                 " + field.getTypename() + "::new",
            "             )",
            "        );",
            "    }"
        );
    }

    private @Mandatory List<String> translateJavaGetterArray(@Mandatory VkComponent component, @Mandatory VkVariable field) {
        String arrayName = arrayGenerator.getName();
        String type = arrayName + "<" + field.getTypename() + ">";
        return new List<>(
            "    public " + type + " " + getMethodName(field) + "() {",
            "        return new " + arrayName + "<>(",
                "            " + getAddressArgument(field) + ",",
                "            " + field.getArray() + ",",
                "            " + field.getTypename() + ".SIZE,",
                "            " + field.getTypename() + "::new",
            "        );",
            "    }"
        );
    }

    public @Mandatory List<String> translateNative(@Mandatory VkComponent component, @Mandatory VkVariable field) {
        String path = vkComponentTranslator.getNativeComponentPath(component);
        String methodName = getOffsetMethodName(field);
        return new List<>(
            "JNIEXPORT jlong JNICALL Java_" + path + methodName + "(JNIEnv* env, jclass clazz) {",
            "    " + component.getName() + " component;",
            "    jlong address = a2l(&component);",
            "    jlong fieldAddress = a2l(&(component." + field.getName() + "));",
            "    return fieldAddress - address;",
            "}"
        );
    }

    private @Mandatory String capitalizeFirst(@Mandatory String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    private @Mandatory String getFullName(@Mandatory VkComponent component, @Mandatory VkVariable field) {
        return component.getName() + "." + field.getName();
    }

    private @Mandatory String getMethodName(@Mandatory VkVariable field) {
        return "get" + capitalizeFirst(field.getName());
    }

    private @Mandatory String getOffsetFieldName(@Mandatory VkVariable field) {
        return field.getName().toUpperCase() + "_OFFSET";
    }

    private @Mandatory String getOffsetMethodName(@Mandatory VkVariable field) {
        return "_get" + capitalizeFirst(field.getName()) + "Offset";
    }

    private @Mandatory String getAddressArgument(@Mandatory VkVariable field) {
        return "address + " + getOffsetFieldName(field);
    }

    private @Mandatory VkVariable transformField(@Mandatory VkVariable field) {
        String altName = nameMap.getOptional(field.getTypename());

        if (altName != null) {
            return new VkVariable(
                altName,
                field.getPointers(),
                field.getName(),
                field.getArray()
            );
        }

        return field;
    }
}
