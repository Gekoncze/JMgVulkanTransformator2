package cz.mg.vulkantransformator.entities.vulkan;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.storage.Value;

public @Entity interface VkComponent {
    @Value
    String getName();
}
