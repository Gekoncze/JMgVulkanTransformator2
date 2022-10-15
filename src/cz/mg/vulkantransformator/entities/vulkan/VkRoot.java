package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.collections.list.List;

public @Entity class VkRoot {
    private List<VkComponent> components = new List<>();

    public VkRoot() {
    }

    @Required @Part
    public List<VkComponent> getComponents() {
        return components;
    }

    public void setComponents(List<VkComponent> components) {
        this.components = components;
    }
}
