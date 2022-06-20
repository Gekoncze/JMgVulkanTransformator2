package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;

public @Entity class VkHandle implements VkComponent {
    private String name;

    public VkHandle() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
