package cz.mg.vulkantransformator.services.parser.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.parser.entities.Typedef;
import cz.mg.vulkantransformator.entities.vulkan.VkFlags;

public @Service class VkFlagsConverter implements VkConverter {
    private static volatile @Service VkFlagsConverter instance;

    public static @Service VkFlagsConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkFlagsConverter();
                }
            }
        }
        return instance;
    }

    private VkFlagsConverter() {
    }

    /**
     * typedef VkFlags VkInstanceCreateFlags
     */
    @Override
    public boolean matches(@Mandatory CMainEntity entity) {
        if (entity instanceof Typedef) {
            Typedef typedef = (Typedef) entity;
            return typedef.getType().getTypename().getName().getText().equals("VkFlags");
        }
        return false;
    }

    @Override
    public @Mandatory VkFlags parse(@Mandatory CMainEntity entity) {
        VkFlags flags = new VkFlags();
        flags.setName(entity.getName().getText());
        return flags;
    }
}