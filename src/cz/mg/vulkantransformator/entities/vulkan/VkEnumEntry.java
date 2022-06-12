package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;

public @Entity class VkEnumEntry implements VkComponent {
    private String key;
    private String value;

    public VkEnumEntry() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
