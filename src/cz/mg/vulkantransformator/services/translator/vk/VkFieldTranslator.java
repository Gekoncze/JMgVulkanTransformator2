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
        return new List<>(
            "    public " + getType(component, field) + " get" + name + "() {",
            "        throw new UnsupportedOperationException();", // TODO
            "    }",
            "",
            "    public void set" + name + "(" + getType(component, field) + " " + field.getName() + ") {",
            "        throw new UnsupportedOperationException();", // TODO
            "    }",
            "",
            "    private static native long _get" + name + "Address(long address);"
        );
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

    private @Mandatory String getType(@Mandatory VkComponent component, @Mandatory VkField field) {
        String target = component.getName() + "." + field.getName();

        if (field.getArray() > 0 && field.getPointers() > 0) {
            throw new UnsupportedOperationException(
                "Unsupported pointer and array count for '" + target + "': " + field.getPointers() + ", " + field.getArray() + "."
            );
        }

        if (field.getArray() == 0) {
            if (field.getPointers() == 0) {
                return field.getTypename();
            } else if (field.getPointers() == 1) {
                if (field.getTypename().equals("void")) {
                    return pointerGenerator.getName() + "<Object>";
                } else {
                    return pointerGenerator.getName() + "<" + field.getTypename() + ">";
                }
            } else if (field.getPointers() == 2) {
                if (field.getTypename().equals("void")) {
                    return pointerGenerator.getName() + "<" + pointerGenerator.getName() + "<Object>>";
                } else {
                    return pointerGenerator.getName() + "<" + pointerGenerator.getName() + "<" + field.getTypename() + ">>";
                }
            } else {
                throw new UnsupportedOperationException(
                    "Unsupported pointer count for '" + target + "': " + field.getPointers() + "."
                );
            }
        } else if (field.getArray() > 0) {
            if (field.getTypename().equals("void")) {
                throw new IllegalArgumentException(
                    "Invalid void array for '" + target + "'."
                );
            } else {
                return arrayGenerator.getName() + "<" + field.getTypename() + ">";
            }
        } else {
            throw new IllegalArgumentException(
                "Illegal array size for '" + target + "': " + field.getArray() + "."
            );
        }
    }
}
