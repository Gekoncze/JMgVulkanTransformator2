package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CMainEntity;
import cz.mg.c.entities.CTypedef;
import cz.mg.vulkantransformator.entities.vulkan.VkFlags;

import java.util.Objects;

public @Service class VkFlagsConverter implements VkConverter {
    private static volatile @Service VkFlagsConverter instance;

    public static @Service VkFlagsConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkFlagsConverter();
                }
            }
        }
        return instance;
    }

    private VkFlagsConverter() {
    }

    /**
     * typedef VkFlags VkInstanceCreateFlags
     */
    @Override
    public boolean matches(@Mandatory CMainEntity entity) {
        if (entity instanceof CTypedef typedef) {
            return Objects.equals(typedef.getType().getTypename().getName(), "VkFlags");
        }
        return false;
    }

    @Override
    public @Mandatory VkFlags convert(@Mandatory CMainEntity entity) {
        VkFlags flags = new VkFlags();
        flags.setName(entity.getName());
        return flags;
    }
}