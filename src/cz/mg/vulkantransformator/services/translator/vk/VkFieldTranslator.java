package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkField;

public @Service class VkFieldTranslator {
    private static @Optional VkFieldTranslator instance;

    public static @Mandatory VkFieldTranslator getInstance() {
        if (instance == null) {
            instance = new VkFieldTranslator();
        }
        return instance;
    }

    private VkFieldTranslator() {
    }

    public @Mandatory List<String> getJavaMethods(@Mandatory VkField field) {
        return new List<>(
            "    public " + field.getTypename() + " get" + capitalizeFirst(field.getName()) + "() {",
            "        throw new UnsupportedOperationException();", // TODO
            "    }",
            "",
            "    public void set" + capitalizeFirst(field.getName()) + "(" + field.getTypename() + " " + field.getName() + ") {",
            "        throw new UnsupportedOperationException();", // TODO
            "    }"
        );
    }

    private @Mandatory String capitalizeFirst(@Mandatory String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
