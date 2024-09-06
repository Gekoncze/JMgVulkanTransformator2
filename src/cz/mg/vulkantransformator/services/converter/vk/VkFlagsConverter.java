package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.CTypedef;
import cz.mg.c.entities.CTypename;
import cz.mg.c.entities.types.CBaseType;
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
    public boolean matches(@Mandatory CEntity entity) {
        return entity instanceof CTypedef typedef
            && typedef.getType() instanceof CBaseType baseType
            && Objects.equals(baseType.getTypename().getName(), "VkFlags");
    }

    @Override
    public @Mandatory VkFlags convert(@Mandatory CEntity entity) {
        VkFlags flags = new VkFlags();
        flags.setName(((CTypename)entity).getName());
        return flags;
    }
}