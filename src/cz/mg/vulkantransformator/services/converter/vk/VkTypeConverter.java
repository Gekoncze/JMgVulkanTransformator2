package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.CTypedef;
import cz.mg.c.entities.CTypename;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.vulkantransformator.entities.vulkan.VkType;

public @Service class VkTypeConverter implements VkConverter {
    private static volatile @Service VkTypeConverter instance;

    public static @Service VkTypeConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkTypeConverter();
                }
            }
        }
        return instance;
    }

    private VkTypeConverter() {
    }

    /**
     * typedef uint64_t VkQueue
     */
    @Override
    public boolean matches(@Mandatory CEntity entity) {
        return entity instanceof CTypedef typedef
            && typedef.getType() instanceof CBaseType baseType
            && baseType.getTypename().getClass() == CTypename.class;
    }

    @Override
    public @Mandatory VkType convert(@Mandatory CEntity entity) {
        VkType type = new VkType();
        type.setName(((CTypename)entity).getName());
        return type;
    }
}