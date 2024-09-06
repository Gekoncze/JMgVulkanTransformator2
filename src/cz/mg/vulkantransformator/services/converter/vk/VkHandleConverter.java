package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.CStruct;
import cz.mg.c.entities.CTypedef;
import cz.mg.c.entities.CTypename;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.vulkantransformator.entities.vulkan.VkHandle;

public @Service class VkHandleConverter implements VkConverter {
    private static volatile @Service VkHandleConverter instance;

    public static @Service VkHandleConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkHandleConverter();
                }
            }
        }
        return instance;
    }

    private VkHandleConverter() {
    }

    /**
     * typedef struct VkQueue_T* VkQueue
     */
    @Override
    public boolean matches(@Mandatory CEntity entity) {
        return entity instanceof CTypedef typedef
            && typedef.getType() instanceof CPointerType pointerType
            && pointerType.getType() instanceof CBaseType baseType
            && baseType.getTypename() instanceof CStruct;
    }

    @Override
    public @Mandatory VkHandle convert(@Mandatory CEntity entity) {
        VkHandle handle = new VkHandle();
        handle.setName(((CTypename)entity).getName());
        return handle;
    }
}