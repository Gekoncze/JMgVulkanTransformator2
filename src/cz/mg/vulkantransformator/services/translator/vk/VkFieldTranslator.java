package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkField;

public @Service class VkFieldTranslator {
    private static @Optional VkFieldTranslator instance;

    public static @Mandatory VkFieldTranslator getInstance() {
        if (instance == null) {
            instance = new VkFieldTranslator();
            instance.vkComponentTranslator = VkComponentTranslator.getInstance();
        }
        return instance;
    }

    private VkComponentTranslator vkComponentTranslator;

    private VkFieldTranslator() {
    }

    public @Mandatory List<String> translateJava(@Mandatory VkField field) {
        String name = capitalizeFirst(field.getName());
        return new List<>(
            "    public " + getType(field) + " get" + name + "() {",
            "        throw new UnsupportedOperationException();", // TODO
            "    }",
            "",
            "    public void set" + name + "(" + getType(field) + " " + field.getName() + ") {",
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

    private @Mandatory String getType(@Mandatory VkField field) {
        if (field.getArray() > 0 && field.getPointers() > 0) {
            throw new UnsupportedOperationException(
                "Unsupported pointer and array count for field translation: " + field.getPointers() + ", " + field.getArray() + "."
            );
        }

        if (field.getPointers() == 0) {
            return field.getTypename();
        } else if (field.getPointers() == 1) {
            if (field.getTypename().equals("void")) {
                return "VkPointer<Object>";
            } else {
                return "VkPointer<" + field.getTypename() + ">";
            }
        } else if (field.getPointers() == 2) {
            if (field.getTypename().equals("void")) {
                return "VkPointer<VkPointer<Object>>";
            } else {
                return "VkPointer<VkPointer<" + field.getTypename() + ">>";
            }
        } else {
            throw new UnsupportedOperationException(
                "Unsupported pointer count for field translation: " + field.getPointers() + "."
            );
        }
    }
}
