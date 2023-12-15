package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.Enum;
import cz.mg.c.parser.entities.EnumEntry;
import cz.mg.c.parser.entities.Typedef;
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
        if (entity instanceof Typedef) {
            Typedef typedef = (Typedef) entity;
            return typedef.getType().getTypename() instanceof Enum;
        }
        return false;
    }

    @Override
    public @Mandatory VkEnum parse(@Mandatory CMainEntity entity) {
        VkEnum vkEnum = new VkEnum();
        vkEnum.setName(entity.getName().getText());
        Typedef typedef = (Typedef) entity;
        Enum enom = (Enum) typedef.getType().getTypename();
        for (EnumEntry enumEntry : Objects.requireNonNull(enom.getEntries())) {
            vkEnum.getEntries().addLast(enumEntryParser.convert(enumEntry));
        }
        return vkEnum;
    }
}
