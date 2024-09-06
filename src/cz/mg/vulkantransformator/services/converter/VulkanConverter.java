package cz.mg.vulkantransformator.services.converter;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.CEntity;
import cz.mg.c.entities.CFile;
import cz.mg.c.entities.CNamed;
import cz.mg.c.entities.macro.Macros;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.converter.vk.*;

public @Service class VulkanConverter {
    private static final @Mandatory List<String> BLACKLIST = new List<>(
        "VkRemoteAddressNV"
    );

    private static volatile @Service VulkanConverter instance;

    public static @Service VulkanConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VulkanConverter();
                    instance.parsers = new List<>(
                        VkStructureConverter.getInstance(),
                        VkUnionConverter.getInstance(),
                        VkEnumConverter.getInstance(),
                        VkFlagsConverter.getInstance(),
                        VkHandleConverter.getInstance(),
                        VkTypeConverter.getInstance(),
                        VkOtherTypeConverter.getInstance(),
                        VkFunctionConverter.getInstance(),
                        VkFunctionPointerConverter.getInstance(),
                        VkVariableConverter.getInstance()
                    );
                    instance.constantConverter = VkConstantConverter.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service List<VkConverter> parsers;
    private @Service VkConstantConverter constantConverter;

    private VulkanConverter() {
    }

    public @Mandatory VkRoot convert(@Mandatory CFile file, @Mandatory Macros macros) {
        VkRoot root = new VkRoot();

        for (CEntity entity : file.getEntities()) {
            VkConverter converter = findMatchingConverter(entity);
            if (converter != null) {
                root.getComponents().addLast(converter.convert(entity));
            }
        }

        root.getComponents().addCollectionLast(
            constantConverter.convert(macros)
        );

        return root;
    }

    private @Optional VkConverter findMatchingConverter(@Mandatory CEntity entity) {
        for (VkConverter parser : parsers) {
            if (parser.matches(entity)) {
                return parser;
            }
        }

        String name = entity instanceof CNamed namedEntity && namedEntity.getName() != null
            ? namedEntity.getName()
            : "<anonymous>";

        if (!BLACKLIST.contains(name)) {
            throw new UnsupportedOperationException(
                "Could not find parser for " + name + " of type " + entity.getClass().getSimpleName() + "."
            );
        }

        return null;
    }
}