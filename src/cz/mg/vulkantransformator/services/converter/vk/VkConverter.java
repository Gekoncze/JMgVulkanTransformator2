package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;

public @Service interface VkConverter {
    boolean matches(@Mandatory CEntity entity);
    @Mandatory VkComponent convert(@Mandatory CEntity entity);
}