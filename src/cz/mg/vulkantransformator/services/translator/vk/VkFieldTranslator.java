package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkField;
import cz.mg.vulkantransformator.services.translator.vk.generators.CArrayGenerator;
import cz.mg.vulkantransformator.services.translator.vk.generators.CPointerGenerator;

public @Service class VkFieldTranslator {
    private static @Optional VkFieldTranslator instance;

    public static @Mandatory VkFieldTranslator getInstance() {
        if (instance == null) {
            instance = new VkFieldTranslator();
            instance.vkComponentTranslator = VkComponentTranslator.getInstance();
            instance.pointerGenerator = CPointerGenerator.getInstance();
            instance.arrayGenerator = CArrayGenerator.getInstance();
        }
        return instance;
    }

    private VkComponentTranslator vkComponentTranslator;
    private CPointerGenerator pointerGenerator;
    private CArrayGenerator arrayGenerator;

    private VkFieldTranslator() {
    }

    public @Mandatory List<String> translateJava(@Mandatory VkComponent component, @Mandatory VkField field) {
        String name = capitalizeFirst(field.getName());
        List<String> lines = new List<>();
        lines.addCollectionLast(translateJavaGetter(component, field));
        lines.addLast("");
        lines.addLast("    private static native long _get" + name + "Address(long address);");
        return lines;

    }

    private @Mandatory List<String> translateJavaGetter(@Mandatory VkComponent component, @Mandatory VkField field) {
        String target = component.getName() + "." + field.getName();

        if (field.getArray() > 0 && field.getPointers() > 0) {
            throw new UnsupportedOperationException(
                "Unsupported pointer and array options for '" + target + "': " + field.getPointers() + ", " + field.getArray() + "."
            );
        }

        if (field.getTypename().equals("void") && field.getPointers() < 1) {
            throw new IllegalArgumentException(
                "Invalid void field for '" + target + "'."
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
                "Unsupported pointer and array options for '" + target + "': " + field.getPointers() + ", " + field.getArray() + "."
            );
        }
    }

    private @Mandatory List<String> translateJavaGetterValue(@Mandatory VkComponent component, @Mandatory VkField field) {
        String type = getTypename(field);
        String getterName = "get" + capitalizeFirst(field.getName());
        String getterAddressName = "_" + getterName + "Address";
        return new List<>(
            "    public " + type + " " + getterName + "() {",
            "        return new " + type + "(" + getterAddressName + "(address));",
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
        String type = pointerGenerator.getName() + "<" + getTypename(field) + ">";
        return new List<>(); // TODO
    }

    private @Mandatory List<String> translateJavaGetterPointer2D(@Mandatory VkComponent component, @Mandatory VkField field) {
        String type = pointerGenerator.getName() + "<" + pointerGenerator.getName() + "<" + getTypename(field) + ">>";
        return new List<>(); // TODO
    }

    private @Mandatory List<String> translateJavaGetterArray(@Mandatory VkComponent component, @Mandatory VkField field) {
        String type = arrayGenerator.getName() + "<" + getTypename(field) + ">";
        return new List<>(); // TODO
    }

    public @Mandatory List<String> translateNative(@Mandatory VkComponent component, @Mandatory VkField field) {
        String name = capitalizeFirst(field.getName());
        String path = vkComponentTranslator.getNativeComponentPath(component);
        return new List<>(
            "JNIEXPORT jlong JNICALL Java_" + path + "_get" + name + "Address(JNIEnv* env, jclass clazz, jlong address) {",
            "    " + component.getName() + "* component = (" + component.getName() + "*) l2a(address);",
            "    return &(component->" + field.getName() + ");",
            "}"
        );
    }

    private @Mandatory String capitalizeFirst(@Mandatory String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    private @Mandatory String getTypename(@Mandatory VkField field) {
        return field.getTypename().equals("void") ? "Object" : field.getTypename();
    }
}
