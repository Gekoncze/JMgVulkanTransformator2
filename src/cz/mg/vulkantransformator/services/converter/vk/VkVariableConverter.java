package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.*;
import cz.mg.c.entities.types.*;
import cz.mg.collections.list.List;
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
                    instance.numberParser = NumberParser.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service NumberParser numberParser;

    private VkVariableConverter() {
    }

    @Override
    public boolean matches(@Mandatory CEntity entity) {
        return entity instanceof CVariable;
    }

    @Override
    public @Mandatory VkVariable convert(@Mandatory CEntity entity) {
        return convertLocal((CVariable) entity);
    }

    public @Mandatory VkVariable convertLocal(@Mandatory CVariable variable) {
        VkVariable vkVariable = new VkVariable();
        vkVariable.setName(variable.getName());
        vkVariable.setTypename(convertBaseType(variable.getType()));
        vkVariable.setPointers(convertPointerTypes(variable.getType()));
        vkVariable.setArrays(convertArrayTypes(variable.getType()));
        return vkVariable;
    }

    public @Mandatory VkVariable convertLocal(@Mandatory CType type) {
        VkVariable vkVariable = new VkVariable();
        vkVariable.setName("");
        vkVariable.setTypename(convertBaseType(type));
        vkVariable.setPointers(convertPointerTypes(type));
        vkVariable.setArrays(convertArrayTypes(type));
        return vkVariable;
    }

    private @Mandatory String convertBaseType(@Mandatory CType type) {
        while (type != null) {
            if (type instanceof CWrapperType wrapperType) {
                type = wrapperType.getType();
            } else if (type instanceof CBaseType baseType) {
                if (baseType.getTypename().getName() != null) {
                    return baseType.getTypename().getName();
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
        throw new IllegalArgumentException();
    }

    private @Mandatory List<VkPointer> convertPointerTypes(@Mandatory CType type) {
        boolean arraysAllowed = true;

        List<VkPointer> pointers = new List<>();
        while (type != null) {
            if (type instanceof CWrapperType wrapperType) {
                if (type instanceof CArrayType) {
                    if (!arraysAllowed) {
                        throw new IllegalArgumentException();
                    }
                } else if (type instanceof CPointerType pointerType) {
                    arraysAllowed = false;
                    pointers.addLast(new VkPointer(pointerType.getModifiers().contains(CModifier.CONST)));
                } else {
                    throw new IllegalArgumentException();
                }
                type = wrapperType.getType();
            } else {
                break;
            }
        }
        return pointers;
    }

    private @Mandatory List<VkArray> convertArrayTypes(@Mandatory CType type) {
        boolean arraysAllowed = true;

        List<VkArray> arrays = new List<>();
        while (type != null) {
            if (type instanceof CWrapperType wrapperType) {
                if (type instanceof CArrayType arrayType) {
                    if (!arraysAllowed) {
                        throw new IllegalArgumentException();
                    }
                    arrays.addLast(new VkArray(numberParser.parse(arrayType.getExpression())));
                } else if (type instanceof CPointerType) {
                    arraysAllowed = false;
                } else {
                    throw new IllegalArgumentException();
                }
                type = wrapperType.getType();
            } else {
                break;
            }
        }
        return arrays;
    }
}