package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CEnum;
import cz.mg.c.entities.CEnumEntry;
import cz.mg.c.entities.CMainEntity;
import cz.mg.c.entities.CTypedef;
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
    public boolean matches(@Mandatory CMainEntity entity) {
        if (entity instanceof CTypedef typedef) {
            return typedef.getType().getTypename() instanceof CEnum;
        }
        return false;
    }

    @Override
    public @Mandatory VkEnum parse(@Mandatory CMainEntity entity) {
        VkEnum vkEnum = new VkEnum();
        vkEnum.setName(entity.getName());
        CTypedef typedef = (CTypedef) entity;
        CEnum enom = (CEnum) typedef.getType().getTypename();
        for (CEnumEntry enumEntry : Objects.requireNonNull(enom.getEntries())) {
            vkEnum.getEntries().addLast(enumEntryParser.convert(enumEntry));
        }
        return vkEnum;
    }
}