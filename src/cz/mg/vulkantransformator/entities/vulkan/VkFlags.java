package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;

public @Entity class VkFlags implements VkComponent {
    private String name;

    public VkFlags() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}