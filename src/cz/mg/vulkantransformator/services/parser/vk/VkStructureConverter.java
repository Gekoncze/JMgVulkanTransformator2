package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.Struct;
import cz.mg.c.parser.entities.Typedef;
import cz.mg.c.parser.entities.Variable;
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
    public boolean matches(@Mandatory CMainEntity entity) {
        if (entity instanceof Typedef) {
            Typedef typedef = (Typedef) entity;
            return typedef.getType().getTypename() instanceof Struct
                && ((Struct) typedef.getType().getTypename()).getVariables() != null;
        }
        return false;
    }

    @Override
    public @Mandatory VkStructure parse(@Mandatory CMainEntity entity) {
        VkStructure structure = new VkStructure();
        structure.setName(entity.getName().getText());
        Typedef typedef = (Typedef) entity;
        Struct struct = (Struct) typedef.getType().getTypename();
        for (Variable variable : Objects.requireNonNull(struct.getVariables())) {
            structure.getFields().addLast(variableConverter.convert(variable));
        }
        return structure;
    }
}
