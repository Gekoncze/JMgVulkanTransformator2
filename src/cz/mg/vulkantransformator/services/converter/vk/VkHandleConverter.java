package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.CStruct;
import cz.mg.c.parser.entities.CTypedef;
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
    public boolean matches(@Mandatory CMainEntity entity) {
        if (entity instanceof CTypedef) {
            CTypedef typedef = (CTypedef) entity;
            boolean isStruct = typedef.getType().getTypename() instanceof CStruct;
            boolean isPointer = typedef.getType().getPointers().count() == 1;
            return isStruct && isPointer;
        }
        return false;
    }

    @Override
    public @Mandatory VkHandle parse(@Mandatory CMainEntity entity) {
        VkHandle handle = new VkHandle();
        handle.setName(entity.getName().getText());
        return handle;
    }
}
