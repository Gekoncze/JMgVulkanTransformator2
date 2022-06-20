package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.utilities.code.Statement;

public @Service interface VkParser {
    boolean matches(@Mandatory Statement statement);
    @Mandatory VkComponent parse(@Mandatory Statement statement);
}
