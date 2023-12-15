package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CFunction;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.CVariable;
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
    public boolean matches(@Mandatory CMainEntity entity) {
        return entity instanceof CFunction;
    }

    @Override
    public @Mandatory VkFunction parse(@Mandatory CMainEntity entity) {
        VkFunction vkFunction = new VkFunction();
        vkFunction.setName(entity.getName().getText());
        CFunction function = (CFunction) entity;
        vkFunction.setOutput(variableConverter.convert(function.getOutput()));
        for (CVariable variable : function.getInput()) {
            vkFunction.getInput().addLast(variableConverter.convert(variable));
        }
        return vkFunction;
    }
}
