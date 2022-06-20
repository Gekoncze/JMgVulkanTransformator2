package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;

public @Service interface VkTranslator<C extends VkComponent> {
    Class<? extends VkComponent> targetClass();
    File translateJava(C component);
    File translateNative(C component);
}
