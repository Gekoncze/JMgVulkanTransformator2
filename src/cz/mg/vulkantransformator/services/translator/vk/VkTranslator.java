package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.services.translator.Index;

public @Service interface VkTranslator<C extends VkComponent> {
    @Mandatory Class<? extends VkComponent> targetClass();
    @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory C component);
    @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory C component);
}
