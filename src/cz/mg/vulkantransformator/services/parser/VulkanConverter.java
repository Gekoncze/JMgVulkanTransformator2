package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CFile;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.preprocessor.processors.macro.entities.Macro;
import cz.mg.c.preprocessor.processors.macro.entities.Macros;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;
import cz.mg.tokenizer.entities.Token;
import cz.mg.vulkantransformator.entities.vulkan.VkConstant;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.parser.vk.*;

public @Service class VulkanConverter {
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
                        VkFunctionConverter.getInstance(),
                        VkFunctionPointerConverter.getInstance()
                    );
                    instance.joiner = StringJoiner.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service List<VkConverter> parsers;
    private @Service StringJoiner joiner;

    private VulkanConverter() {
    }

    public @Mandatory VkRoot convert(@Mandatory CFile file, @Mandatory Macros macros) {
        VkRoot root = new VkRoot();

        for (CMainEntity entity : file.getEntities()) {
            root.getComponents().addLast(findMatchingParser(entity).parse(entity));
        }

        for (Macro macro : macros.getDefinitions())
        {
            if (macro.getParameters() == null && !macro.getTokens().isEmpty())
            {
                VkConstant constant = new VkConstant();
                constant.setName(macro.getName().getText());
                constant.setValue(joiner.join(macro.getTokens(), "", Token::getText));
                root.getComponents().addLast(constant);
            }
        }

        return root;
    }

    private @Mandatory VkConverter findMatchingParser(@Mandatory CMainEntity entity) {
        for (VkConverter parser : parsers) {
            if (parser.matches(entity)) {
                return parser;
            }
        }
        throw new UnsupportedOperationException(
            "Could not find parser for " + entity.getName().getText()
                + " of type " + entity.getClass().getSimpleName() + "."
        );
    }
}
