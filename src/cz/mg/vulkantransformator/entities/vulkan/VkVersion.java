package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
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

    @Value
    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    @Value
    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }
}
