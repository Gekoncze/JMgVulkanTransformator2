package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;

public @Service interface VkFileGenerator {
    @Mandatory String getSourceFileName();
    @Mandatory List<File> generateFiles(@Mandatory VkRoot root);
}
