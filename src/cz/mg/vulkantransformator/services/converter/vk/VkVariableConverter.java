package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.CArray;
import cz.mg.c.entities.CMainEntity;
import cz.mg.c.entities.CType;
import cz.mg.c.entities.CVariable;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;
import cz.mg.token.Token;
import cz.mg.tokenizer.exceptions.TraceableException;
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
        vkVariable.setPointers(variable.getType().getPointers().count());
        vkVariable.setArray(convertArrays(variable.getType().getArrays(), variable.getName()));
        return vkVariable;
    }

    public @Mandatory VkVariable convertLocal(@Mandatory CType type) {
        VkVariable vkVariable = new VkVariable();
        vkVariable.setName("");
        vkVariable.setTypename(type.getTypename().getName());
        vkVariable.setPointers(type.getPointers().count());
        vkVariable.setArray(convertArrays(type.getArrays(), type.getTypename().getName()));
        return vkVariable;
    }

    private int convertArrays(@Mandatory List<CArray> arrays, @Optional String name) {
        if (arrays.count() <= 0) {
            return 0;
        } else if (arrays.count() == 1) {
            CArray array = arrays.getFirst();
            String expression = joiner.join(array.getExpression(), "", Token::getText);
            return numberParser.parse(expression);
        } else {
            throw new UnsupportedOperationException(
                "Unsupported multi-dimensional array " + (name == null ? "<anonymous>" : name) + "."
            );
        }
    }
}