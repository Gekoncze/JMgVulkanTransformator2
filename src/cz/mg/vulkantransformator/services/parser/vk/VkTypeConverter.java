package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.CTypedef;
import cz.mg.c.parser.entities.CTypename;
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
        if (entity instanceof CTypedef) {
            CTypedef typedef = (CTypedef) entity;
            return typedef.getType().getTypename().getClass().equals(CTypename.class);
        }
        return false;
    }

    @Override
    public @Mandatory VkType parse(@Mandatory CMainEntity entity) {
        VkType type = new VkType();
        type.setName(entity.getName().getText());
        return type;
    }
}
