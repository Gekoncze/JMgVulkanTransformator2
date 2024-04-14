package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

public @Entity class VkArray {
    private int count;

    public VkArray() {
    }

    public VkArray(int count) {
        this.count = count;
    }

    @Required @Value
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
