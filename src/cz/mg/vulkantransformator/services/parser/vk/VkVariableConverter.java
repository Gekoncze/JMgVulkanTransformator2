package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.Array;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.entities.Variable;
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

    public @Mandatory VkVariable convert(@Mandatory Variable variable) {
        VkVariable vkVariable = new VkVariable();
        vkVariable.setName(variable.getName().getText());
        vkVariable.setTypename(variable.getType().getTypename().getName().getText());
        vkVariable.setPointers(variable.getType().getPointers().count());
        vkVariable.setArray(convertArrays(variable.getType().getArrays()));
        return vkVariable;
    }

    public @Mandatory VkVariable convert(@Mandatory Type type) {
        VkVariable vkVariable = new VkVariable();
        vkVariable.setName("");
        vkVariable.setTypename(type.getTypename().getName().getText());
        vkVariable.setPointers(type.getPointers().count());
        vkVariable.setArray(convertArrays(type.getArrays()));
        return vkVariable;
    }

    private int convertArrays(@Mandatory List<Array> arrays) {
        if (arrays.count() == 0) {
            return 0;
        } else if (arrays.count() == 1) {
            Array array = arrays.getFirst();
            return Integer.parseInt(joiner.join(array.getExpression(), "", Token::getText));
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
