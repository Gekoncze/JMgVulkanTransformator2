package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.CEnum;
import cz.mg.c.entities.CEnumEntry;
import cz.mg.c.entities.CTypedef;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.vulkantransformator.entities.vulkan.VkEnum;

import java.util.Objects;

public @Service class VkEnumConverter implements VkConverter {
    private static volatile @Service VkEnumConverter instance;

    public static @Mandatory VkEnumConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkEnumConverter();
                    instance.enumEntryParser = VkEnumEntryConverter.getInstance();
                }
            }
        }
        return instance;
    }

    private VkEnumEntryConverter enumEntryParser;

    private VkEnumConverter() {
    }

    /**
     * typedef enum VkStencilFaceFlagBits {
     *     VK_STENCIL_FACE_FRONT_BIT = 0x00000001,
     *     VK_STENCIL_FACE_BACK_BIT = 0x00000002,
     *     VK_STENCIL_FACE_FRONT_AND_BACK = 0x00000003,
     *     VK_STENCIL_FRONT_AND_BACK = VK_STENCIL_FACE_FRONT_AND_BACK,
     *     VK_STENCIL_FACE_FLAG_BITS_MAX_ENUM = 0x7FFFFFFF
     * } VkStencilFaceFlagBits
     */
    @Override
    public boolean matches(@Mandatory CEntity entity) {
        return entity instanceof CTypedef typedef
            && typedef.getType() instanceof CBaseType baseType
            && baseType.getTypename() instanceof CEnum;
    }

    @Override
    public @Mandatory VkEnum convert(@Mandatory CEntity entity) {
        CTypedef typedef = (CTypedef) entity;
        CEnum cEnum = (CEnum) ((CBaseType)typedef.getType()).getTypename();

        VkEnum vkEnum = new VkEnum();
        vkEnum.setName(typedef.getName());

        for (CEnumEntry enumEntry : Objects.requireNonNull(cEnum.getEntries())) {
            vkEnum.getEntries().addLast(enumEntryParser.convert(enumEntry));
        }

        return vkEnum;
    }
}