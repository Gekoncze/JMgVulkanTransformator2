package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;

public @Entity class VkEnum implements VkComponent {
    private String name;
    private List<VkEnumEntry> entries = new List<>();

    public VkEnum() {
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
    public List<VkEnumEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<VkEnumEntry> entries) {
        this.entries = entries;
    }
}
