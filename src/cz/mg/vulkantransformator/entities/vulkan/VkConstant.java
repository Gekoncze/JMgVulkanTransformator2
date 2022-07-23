package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

public @Entity class VkConstant implements VkComponent {
    private String name;
    private String value;

    public VkConstant() {
    }

    @Override
    @Required @Value
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Required @Value
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
