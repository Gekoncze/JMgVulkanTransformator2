package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.map.Map;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;

public @Utility class Index {
    private final Map<String, VkComponent> components;

    public Index(@Mandatory VkRoot root) {
        this.components = new Map<>(200);

        for (VkComponent component : root.getComponents()) {
            this.components.set(component.getName(), component);
        }
    }

    public @Mandatory VkComponent getComponent(@Mandatory String name) {
        return components.get(name);
    }
}
