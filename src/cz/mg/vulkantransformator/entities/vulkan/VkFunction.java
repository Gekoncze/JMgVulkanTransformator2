package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.collections.list.List;

public @Entity class VkFunction implements VkComponent {
    private String name;
    private VkVariable output;
    private List<VkVariable> input;

    public VkFunction() {
    }

    @Override
    @Required @Part
    public String getName() {
        return name;
    }

    @Optional @Part
    public VkVariable getOutput() {
        return output;
    }

    @Mandatory @Part
    public List<VkVariable> getInput() {
        return input;
    }
}
