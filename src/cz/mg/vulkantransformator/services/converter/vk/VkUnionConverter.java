package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.CTypedef;
import cz.mg.c.entities.CUnion;
import cz.mg.c.entities.CVariable;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.vulkantransformator.entities.vulkan.VkUnion;

import java.util.Objects;

public @Service class VkUnionConverter implements VkConverter {
    private static volatile @Service VkUnionConverter instance;

    public static @Service VkUnionConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkUnionConverter();
                    instance.variableConverter = VkVariableConverter.getInstance();
                }
            }
        }
        return instance;
    }

    private VkVariableConverter variableConverter;

    private VkUnionConverter() {
    }

    /**
     * typedef union VkClearColorValue {
     *     float       float32[4];
     *     int32_t     int32[4];
     *     uint32_t    uint32[4];
     * } VkClearColorValue
     */
    @Override
    public boolean matches(@Mandatory CEntity entity) {
        return entity instanceof CTypedef typedef
            && typedef.getType() instanceof CBaseType baseType
            && baseType.getTypename() instanceof CUnion;
    }

    @Override
    public @Mandatory VkUnion convert(@Mandatory CEntity entity) {
        CTypedef typedef = (CTypedef) entity;
        CUnion union = (CUnion) ((CBaseType)typedef.getType()).getTypename();

        VkUnion vkUnion = new VkUnion();
        vkUnion.setName(typedef.getName());

        for (CVariable variable : Objects.requireNonNull(union.getVariables())) {
            vkUnion.getFields().addLast(variableConverter.convertLocal(variable));
        }

        return vkUnion;
    }
}