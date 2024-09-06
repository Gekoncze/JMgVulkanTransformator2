package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.CVariable;
import cz.mg.vulkantransformator.entities.vulkan.VkFunction;

public @Service class VkFunctionConverter implements VkConverter {
    private static volatile @Service VkFunctionConverter instance;

    public static @Service VkFunctionConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkFunctionConverter();
                    instance.variableConverter = VkVariableConverter.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service VkVariableConverter variableConverter;

    private VkFunctionConverter() {
    }

    /**
     * VKAPI_ATTR VkResult VKAPI_CALL vkCreateInstance(
     * const VkInstanceCreateInfo*                 pCreateInfo,
     * const VkAllocationCallbacks*                pAllocator,
     * VkInstance*                                 pInstance)
     */
    @Override
    public boolean matches(@Mandatory CEntity entity) {
        return entity instanceof CFunction;
    }

    @Override
    public @Mandatory VkFunction convert(@Mandatory CEntity entity) {
        CFunction function = (CFunction) entity;

        VkFunction vkFunction = new VkFunction();
        vkFunction.setName(function.getName());
        vkFunction.setOutput(variableConverter.convertLocal(function.getOutput()));

        for (CVariable variable : function.getInput()) {
            vkFunction.getInput().addLast(variableConverter.convertLocal(variable));
        }

        return vkFunction;
    }
}