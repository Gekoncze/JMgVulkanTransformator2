package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;

public @Entity class VkEnumEntry implements VkComponent {
    private String name;
    private List<String> expression = new List<>();

    public VkEnumEntry() {
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
    public List<String> getExpression() {
        return expression;
    }

    public void setExpression(List<String> expression) {
        this.expression = expression;
    }
}
