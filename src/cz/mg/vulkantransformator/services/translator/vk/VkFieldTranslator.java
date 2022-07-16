package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkField;
import cz.mg.vulkantransformator.services.translator.vk.generators.CArrayGenerator;
import cz.mg.vulkantransformator.services.translator.vk.generators.CPointerGenerator;
import cz.mg.vulkantransformator.services.translator.vk.generators.CVoidGenerator;

public @Service class VkFieldTranslator {
    private static @Optional VkFieldTranslator instance;

    public static @Mandatory VkFieldTranslator getInstance() {
        if (instance == null) {
            instance = new VkFieldTranslator();
            instance.vkComponentTranslator = VkComponentTranslator.getInstance();
            instance.pointerGenerator = CPointerGenerator.getInstance();
            instance.arrayGenerator = CArrayGenerator.getInstance();
            instance.voidGenerator = CVoidGenerator.getInstance();
        }
        return instance;
    }

    private VkComponentTranslator vkComponentTranslator;
    private CPointerGenerator pointerGenerator;
    private CArrayGenerator arrayGenerator;
    private CVoidGenerator voidGenerator;

    private VkFieldTranslator() {
    }

    public @Mandatory List<String> translateJava(@Mandatory VkComponent component, @Mandatory VkField field) {
        String offsetFieldName = getOffsetFieldName(field);
        String offsetMethodName = getOffsetMethodName(field);
        List<String> lines = new List<>();
        lines.addLast("    private static final long " + offsetFieldName + " = " + offsetMethodName + "();");
        lines.addLast("");
        lines.addCollectionLast(translateJavaGetter(component, field));
        lines.addLast("");
        lines.addLast("    private static native long " + offsetMethodName + "();");
        return lines;

    }

    private @Mandatory List<String> translateJavaGetter(@Mandatory VkComponent component, @Mandatory VkField field) {
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

    private @Mandatory List<String> translateJavaGetterValue(@Mandatory VkComponent component, @Mandatory VkField field) {
        String type = field.getTypename();
        String methodName = getMethodName(field);
        String offsetFieldName = getOffsetFieldName(field);
        return new List<>(
            "    public " + type + " " + methodName + "() {",
            "        return new " + type + "(address + " + offsetFieldName + ");",
            "    }"
        );
    }

    private @Mandatory List<String> translateJavaGetterPointer(@Mandatory VkComponent component, @Mandatory VkField field) {
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

    private @Mandatory List<String> translateJavaGetterPointer1D(@Mandatory VkComponent component, @Mandatory VkField field) {
        String pointerTypeName = pointerGenerator.getName();
        String typeName = getTypename(field);
        String type = pointerTypeName + "<" + typeName + ">";

        return new List<>(
            "    public " + type + " " + getMethodName(field) + "() {",
            "        return new " + pointerTypeName + "<>(",
            "             " + getAddressArgument(field) + ",",
            "             " + typeName + ".TYPE",
            "        );",
            "    }"
        );
    }

    private @Mandatory List<String> translateJavaGetterPointer2D(@Mandatory VkComponent component, @Mandatory VkField field) {
        String pointerTypeName = pointerGenerator.getName();
        String typeName = getTypename(field);
        String type = pointerTypeName + "<" + pointerTypeName + "<" + typeName + ">>";

        return new List<>(
            // TODO
        );
    }

    private @Mandatory List<String> translateJavaGetterArray(@Mandatory VkComponent component, @Mandatory VkField field) {
        String arrayTypeName = arrayGenerator.getName();
        String typeName = getTypename(field);
        String type = arrayTypeName + "<" + typeName + ">";

        return new List<>(
            "    public " + type + " " + getMethodName(field) + "() {",
            "        return new " + arrayTypeName + "<>(",
                "            " + getAddressArgument(field) + ",",
                "            " + field.getArray() + ",",
                "            " + typeName + ".TYPE",
            "        );",
            "    }"
        );
    }

    public @Mandatory List<String> translateNative(@Mandatory VkComponent component, @Mandatory VkField field) {
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

    private @Mandatory String getTypename(@Mandatory VkField field) {
        return isVoid(field) ? voidGenerator.getName() : field.getTypename();
    }

    private @Mandatory String getFullName(@Mandatory VkComponent component, @Mandatory VkField field) {
        return component.getName() + "." + field.getName();
    }

    private @Mandatory String getMethodName(@Mandatory VkField field) {
        return "get" + capitalizeFirst(field.getName());
    }

    private @Mandatory String getOffsetFieldName(@Mandatory VkField field) {
        return field.getName().toUpperCase() + "_OFFSET";
    }

    private @Mandatory String getOffsetMethodName(@Mandatory VkField field) {
        return "_get" + capitalizeFirst(field.getName()) + "Offset";
    }

    private @Mandatory String getAddressArgument(@Mandatory VkField field) {
        return "address + " + getOffsetFieldName(field);
    }

    private boolean isVoid(@Mandatory VkField field) {
        return field.getTypename().equals("void");
    }
}
