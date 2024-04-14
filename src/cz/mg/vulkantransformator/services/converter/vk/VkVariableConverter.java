package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.*;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;
import cz.mg.token.Token;
import cz.mg.tokenizer.exceptions.TraceableException;
import cz.mg.vulkantransformator.entities.vulkan.VkArray;
import cz.mg.vulkantransformator.entities.vulkan.VkPointer;
import cz.mg.vulkantransformator.entities.vulkan.VkVariable;

public @Service class VkVariableConverter implements VkConverter {
    private static volatile @Service VkVariableConverter instance;

    public static @Service VkVariableConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkVariableConverter();
                    instance.joiner = StringJoiner.getInstance();
                    instance.numberParser = NumberParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service StringJoiner joiner;
    private @Service NumberParser numberParser;

    private VkVariableConverter() {
    }

    @Override
    public boolean matches(@Mandatory CMainEntity entity) {
        return entity instanceof CVariable;
    }

    @Override
    public @Mandatory VkVariable convert(@Mandatory CMainEntity entity) {
        return convertLocal((CVariable) entity);
    }

    public @Mandatory VkVariable convertLocal(@Mandatory CVariable variable) {
        VkVariable vkVariable = new VkVariable();
        vkVariable.setName(variable.getName());
        vkVariable.setTypename(variable.getType().getTypename().getName());
        vkVariable.setPointers(convertPointers(variable.getType().getPointers()));
        vkVariable.setArrays(convertArrays(variable.getType().getArrays()));
        return vkVariable;
    }

    public @Mandatory VkVariable convertLocal(@Mandatory CType type) {
        VkVariable vkVariable = new VkVariable();
        vkVariable.setName("");
        vkVariable.setTypename(type.getTypename().getName());
        vkVariable.setPointers(convertPointers(type.getPointers()));
        vkVariable.setArrays(convertArrays(type.getArrays()));
        return vkVariable;
    }

    private @Mandatory List<VkPointer> convertPointers(@Mandatory List<CPointer> pointers) {
        List<VkPointer> vkPointers = new List<>();
        for (CPointer pointer : pointers) {
            vkPointers.addLast(new VkPointer(pointer.isConstant()));
        }
        return vkPointers;
    }

    private @Mandatory List<VkArray> convertArrays(@Mandatory List<CArray> arrays) {
        List<VkArray> vkArrays = new List<>();
        for (CArray array : arrays) {
            String expression = joiner.join(array.getExpression(), "", Token::getText);
            vkArrays.addLast(new VkArray(numberParser.parse(expression)));
        }
        return vkArrays;
    }
}