package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.collections.list.List;

public @Entity class VkFunctionPointer implements VkComponent {
    private String name;
    private VkVariable output;
    private List<VkVariable> input = new List<>();

    public VkFunctionPointer() {
    }

    @Override
    @Required @Part
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Optional @Part
    public VkVariable getOutput() {
        return output;
    }

    public void setOutput(VkVariable output) {
        this.output = output;
    }

    @Mandatory @Part
    public List<VkVariable> getInput() {
        return input;
    }

    public void setInput(List<VkVariable> input) {
        this.input = input;
    }
}
