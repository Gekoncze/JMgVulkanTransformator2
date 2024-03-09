package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CMainEntity;
import cz.mg.c.entities.CStruct;
import cz.mg.vulkantransformator.entities.vulkan.VkType;

public @Service class VkOtherTypeConverter implements VkConverter {
    private static volatile @Service VkOtherTypeConverter instance;

    public static @Service VkOtherTypeConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkOtherTypeConverter();
                }
            }
        }
        return instance;
    }

    private VkOtherTypeConverter() {
    }

    /**
     * struct ANativeWindow
     */
    @Override
    public boolean matches(@Mandatory CMainEntity entity) {
        return entity instanceof CStruct
            && ((CStruct) entity).getVariables() == null;
    }

    @Override
    public @Mandatory VkType parse(@Mandatory CMainEntity entity) {
        VkType type = new VkType();
        type.setName(entity.getName());
        return type;
    }
}