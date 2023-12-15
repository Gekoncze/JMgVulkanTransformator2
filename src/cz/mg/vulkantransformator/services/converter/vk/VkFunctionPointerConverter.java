package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CFunction;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.CTypedef;
import cz.mg.c.parser.entities.CVariable;
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
    public boolean matches(@Mandatory CMainEntity entity) {
        if (entity instanceof CTypedef) {
            CTypedef typedef = (CTypedef) entity;
            return typedef.getType().getTypename() instanceof CFunction
                && typedef.getType().getPointers().count() == 1;
        }
        return false;
    }

    @Override
    public @Mandatory VkFunctionPointer parse(@Mandatory CMainEntity entity) {
        VkFunctionPointer functionPointer = new VkFunctionPointer();
        functionPointer.setName(entity.getName().getText());
        CTypedef typedef = (CTypedef) entity;
        CFunction function = (CFunction) typedef.getType().getTypename();
        functionPointer.setOutput(variableConverter.convert(function.getOutput()));
        for (CVariable variable : function.getInput()) {
            functionPointer.getInput().addLast(variableConverter.convert(variable));
        }
        return functionPointer;
    }
}
