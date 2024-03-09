package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CArray;
import cz.mg.c.entities.CType;
import cz.mg.c.entities.CVariable;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;
import cz.mg.tokenizer.entities.Token;
import cz.mg.vulkantransformator.entities.vulkan.VkVariable;

public @Service class VkVariableConverter {
    private static volatile @Service VkVariableConverter instance;

    public static @Service VkVariableConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkVariableConverter();
                    instance.joiner = StringJoiner.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service StringJoiner joiner;

    private VkVariableConverter() {
    }

    public @Mandatory VkVariable convert(@Mandatory CVariable variable) {
        VkVariable vkVariable = new VkVariable();
        vkVariable.setName(variable.getName());
        vkVariable.setTypename(variable.getType().getTypename().getName());
        vkVariable.setPointers(variable.getType().getPointers().count());
        vkVariable.setArray(convertArrays(variable.getType().getArrays()));
        return vkVariable;
    }

    public @Mandatory VkVariable convert(@Mandatory CType type) {
        VkVariable vkVariable = new VkVariable();
        vkVariable.setName("");
        vkVariable.setTypename(type.getTypename().getName());
        vkVariable.setPointers(type.getPointers().count());
        vkVariable.setArray(convertArrays(type.getArrays()));
        return vkVariable;
    }

    private int convertArrays(@Mandatory List<CArray> arrays) {
        if (arrays.count() == 0) {
            return 0;
        } else if (arrays.count() == 1) {
            CArray array = arrays.getFirst();
            return Integer.parseInt(joiner.join(array.getExpression(), "", Token::getText));
        } else {
            throw new UnsupportedOperationException();
        }
    }
}