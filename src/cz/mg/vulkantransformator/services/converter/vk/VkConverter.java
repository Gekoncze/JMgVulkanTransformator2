package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CMainEntity;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;

public @Service interface VkConverter {
    boolean matches(@Mandatory CMainEntity entity);
    @Mandatory VkComponent parse(@Mandatory CMainEntity entity);
}