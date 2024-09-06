package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.CStruct;
import cz.mg.c.entities.CTypedef;
import cz.mg.c.entities.CVariable;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.vulkantransformator.entities.vulkan.VkStructure;

import java.util.Objects;

public @Service class VkStructureConverter implements VkConverter {
    private static volatile @Service VkStructureConverter instance;

    public static @Service VkStructureConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkStructureConverter();
                    instance.variableConverter = VkVariableConverter.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service VkVariableConverter variableConverter;

    private VkStructureConverter() {
    }

    /**
     * typedef struct VkPipelineColorBlendStateCreateInfo {
     *     VkStructureType                               sType;
     *     const void*                                   pNext;
     *     VkPipelineColorBlendStateCreateFlags          flags;
     *     VkBool32                                      logicOpEnable;
     *     VkLogicOp                                     logicOp;
     *     uint32_t                                      attachmentCount;
     *     const VkPipelineColorBlendAttachmentState*    pAttachments;
     *     float                                         blendConstants[4];
     * } VkPipelineColorBlendStateCreateInfo
     */
    @Override
    public boolean matches(@Mandatory CEntity entity) {
        return entity instanceof CTypedef typedef
            && typedef.getType() instanceof CBaseType baseType
            && baseType.getTypename() instanceof CStruct struct
            && struct.getVariables() != null;
    }

    @Override
    public @Mandatory VkStructure convert(@Mandatory CEntity entity) {
        CTypedef typedef = (CTypedef) entity;
        CStruct struct = (CStruct) ((CBaseType)typedef.getType()).getTypename();

        VkStructure structure = new VkStructure();
        structure.setName(typedef.getName());

        for (CVariable variable : Objects.requireNonNull(struct.getVariables())) {
            structure.getFields().addLast(variableConverter.convertLocal(variable));
        }
        return structure;
    }
}