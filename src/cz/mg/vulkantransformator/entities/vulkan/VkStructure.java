package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;

public @Entity class VkStructure implements VkComponent {
    private String name;
    private List<VkField> fields = new List<>();

    public VkStructure() {
    }

    public VkStructure(String name) {
        this.name = name;
    }

    @Value
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Part
    public List<VkField> getFields() {
        return fields;
    }

    public void setFields(List<VkField> fields) {
        this.fields = fields;
    }
}
