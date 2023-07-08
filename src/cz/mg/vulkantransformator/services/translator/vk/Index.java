package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.components.Capacity;
import cz.mg.collections.map.Map;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;

public @Component class Index {
    private final Map<String, VkComponent> components;

    public Index(@Mandatory VkRoot root) {
        this.components = new Map<>(new Capacity(200));

        for (VkComponent component : root.getComponents()) {
            this.components.set(component.getName(), component);
        }
    }

    public @Mandatory Map<String, VkComponent> getComponents() {
        return components;
    }
}
