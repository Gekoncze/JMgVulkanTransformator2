package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.storage.Part;
import cz.mg.collections.list.List;

public @Entity class VkRoot {
    private VkVersion version;
    private List<VkComponent> components = new List<>();

    public VkRoot() {
    }

    public VkRoot(VkVersion version) {
        this.version = version;
    }

    @Part
    public VkVersion getVersion() {
        return version;
    }

    public void setVersion(VkVersion version) {
        this.version = version;
    }

    @Part
    public List<VkComponent> getComponents() {
        return components;
    }

    public void setComponents(List<VkComponent> components) {
        this.components = components;
    }
}
