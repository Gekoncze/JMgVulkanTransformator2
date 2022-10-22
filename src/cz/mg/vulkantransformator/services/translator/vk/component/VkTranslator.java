package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.services.translator.vk.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Service interface VkTranslator<C extends VkComponent> {
    @Mandatory Class<? extends VkComponent> targetClass();

    default @Mandatory String getJavaName(@Mandatory C component) {
        return component.getName();
    }

    @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory C component,
        @Mandatory LibraryConfiguration configuration
    );

    @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory C component,
        @Mandatory LibraryConfiguration configuration
    );

    @Mandatory List<String> translateNativeHeader(
        @Mandatory Index index,
        @Mandatory C component,
        @Mandatory LibraryConfiguration configuration
    );
}
