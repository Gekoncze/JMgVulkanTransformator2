package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CMainEntity;
import cz.mg.c.entities.CTypedef;
import cz.mg.c.entities.CTypename;
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
    public boolean matches(@Mandatory CMainEntity entity) {
        if (entity instanceof CTypedef typedef) {
            return typedef.getType().getTypename().getClass().equals(CTypename.class);
        }
        return false;
    }

    @Override
    public @Mandatory VkType convert(@Mandatory CMainEntity entity) {
        VkType type = new VkType();
        type.setName(entity.getName());
        return type;
    }
}