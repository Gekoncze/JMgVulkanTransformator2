package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;

public @Service interface VkConverter {
    boolean matches(@Mandatory CMainEntity entity);
    @Mandatory VkComponent parse(@Mandatory CMainEntity entity);
}
