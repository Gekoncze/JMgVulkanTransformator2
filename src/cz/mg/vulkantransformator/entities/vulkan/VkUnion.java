package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;

public @Entity class VkUnion implements VkComponent {
    private String name;
    private List<VkVariable> fields = new List<>();

    public VkUnion() {
    }

    @Override
    @Required @Value
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Required @Part
    public List<VkVariable> getFields() {
        return fields;
    }

    public void setFields(List<VkVariable> fields) {
        this.fields = fields;
    }
}
