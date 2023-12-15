package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.Function;
import cz.mg.c.parser.entities.Typedef;
import cz.mg.c.parser.entities.Variable;
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
        if (entity instanceof Typedef) {
            Typedef typedef = (Typedef) entity;
            return typedef.getType().getTypename() instanceof Function
                && typedef.getType().getPointers().count() == 1;
        }
        return false;
    }

    @Override
    public @Mandatory VkFunctionPointer parse(@Mandatory CMainEntity entity) {
        VkFunctionPointer functionPointer = new VkFunctionPointer();
        functionPointer.setName(entity.getName().getText());
        Typedef typedef = (Typedef) entity;
        Function function = (Function) typedef.getType().getTypename();
        functionPointer.setOutput(variableConverter.convert(function.getOutput()));
        for (Variable variable : function.getInput()) {
            functionPointer.getInput().addLast(variableConverter.convert(variable));
        }
        return functionPointer;
    }
}
