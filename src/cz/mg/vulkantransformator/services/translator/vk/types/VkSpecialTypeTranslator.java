package cz.mg.vulkantransformator.services.translator.vk.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkType;
import cz.mg.vulkantransformator.services.translator.Index;

public @Service interface VkSpecialTypeTranslator {
    @Mandatory String getName();
    @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkType type);
    @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkType type);
}
