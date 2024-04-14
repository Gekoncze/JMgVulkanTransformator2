package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;

public @Entity class VkVariable implements VkComponent {
    private String typename;
    private String name;
    private List<VkPointer> pointers;
    private List<VkArray> arrays;

    public VkVariable() {
    }

    public VkVariable(String typename, String name, List<VkPointer> pointers, List<VkArray> arrays) {
        this.typename = typename;
        this.name = name;
        this.pointers = pointers;
        this.arrays = arrays;
    }

    @Required @Value
    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
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
    public List<VkPointer> getPointers() {
        return pointers;
    }

    public void setPointers(List<VkPointer> pointers) {
        this.pointers = pointers;
    }

    @Required @Part
    public List<VkArray> getArrays() {
        return arrays;
    }

    public void setArrays(List<VkArray> arrays) {
        this.arrays = arrays;
    }
}
