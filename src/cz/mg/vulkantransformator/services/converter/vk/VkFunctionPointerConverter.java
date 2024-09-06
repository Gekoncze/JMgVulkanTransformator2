package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.CTypedef;
import cz.mg.c.entities.CVariable;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.vulkantransformator.entities.vulkan.VkFunctionPointer;

public @Service class VkFunctionPointerConverter implements VkConverter {
    private static volatile @Service VkFunctionPointerConverter instance;

    public static @Service VkFunctionPointerConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkFunctionPointerConverter();
                    instance.variableConverter = VkVariableConverter.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service VkVariableConverter variableConverter;

    private VkFunctionPointerConverter() {
    }

    /**
     * typedef void (VKAPI_PTR *PFN_vkFreeFunction)(
     * void*                                       pUserData,
     * void*                                       pMemory);
     */
    @Override
    public boolean matches(@Mandatory CEntity entity) {
        return entity instanceof CTypedef typedef
            && typedef.getType() instanceof CPointerType pointerType
            && pointerType.getType() instanceof CBaseType baseType
            && baseType.getTypename() instanceof CFunction;
    }

    @Override
    public @Mandatory VkFunctionPointer convert(@Mandatory CEntity entity) {
        CTypedef typedef = (CTypedef) entity;
        CPointerType pointerType = (CPointerType) typedef.getType();
        CBaseType baseType = (CBaseType) pointerType.getType();
        CFunction function = (CFunction) baseType.getTypename();

        VkFunctionPointer functionPointer = new VkFunctionPointer();
        functionPointer.setName(typedef.getName());

        functionPointer.setOutput(variableConverter.convertLocal(function.getOutput()));
        for (CVariable variable : function.getInput()) {
            functionPointer.getInput().addLast(variableConverter.convertLocal(variable));
        }
        return functionPointer;
    }
}