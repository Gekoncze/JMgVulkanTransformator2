package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Value;

public @Entity class VkVersion {
    private Integer major;
    private Integer minor;

    public VkVersion() {
    }

    public VkVersion(Integer major, Integer minor) {
        this.major = major;
        this.minor = minor;
    }

    @Required @Value
    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    @Required @Value
    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }
}
