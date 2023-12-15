package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.Typedef;
import cz.mg.c.parser.entities.Union;
import cz.mg.c.parser.entities.Variable;
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
    public boolean matches(@Mandatory CMainEntity entity) {
        if (entity instanceof Typedef) {
            Typedef typedef = (Typedef) entity;
            return typedef.getType().getTypename() instanceof Union;
        }
        return false;
    }

    @Override
    public @Mandatory VkUnion parse(@Mandatory CMainEntity entity) {
        VkUnion vkUnion = new VkUnion();
        vkUnion.setName(entity.getName().getText());
        Typedef typedef = (Typedef) entity;
        Union union = (Union) typedef.getType().getTypename();
        for (Variable variable : Objects.requireNonNull(union.getVariables())) {
            vkUnion.getFields().addLast(variableConverter.convert(variable));
        }
        return vkUnion;
    }
}
