package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;

public @Entity class VkEnumEntry implements VkComponent {
    private String name;
    private String value;

    public VkEnumEntry() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
