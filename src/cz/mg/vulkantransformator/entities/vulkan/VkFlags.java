package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

public @Entity class VkFlags implements VkComponent {
    private String name;

    public VkFlags() {
    }

    @Override
    @Required @Value
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
