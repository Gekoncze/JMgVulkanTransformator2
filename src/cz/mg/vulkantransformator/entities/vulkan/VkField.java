package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.storage.Value;

public @Entity class VkField implements VkComponent {
    private String typename;
    private Integer pointers;
    private String name;
    private Integer array;

    public VkField() {
    }

    public VkField(String typename, Integer pointers, String name, Integer array) {
        this.typename = typename;
        this.pointers = pointers;
        this.name = name;
        this.array = array;
    }

    @Value
    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    @Value
    public Integer getPointers() {
        return pointers;
    }

    public void setPointers(Integer pointers) {
        this.pointers = pointers;
    }

    @Value
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Value
    public Integer getArray() {
        return array;
    }

    public void setArray(Integer array) {
        this.array = array;
    }
}
