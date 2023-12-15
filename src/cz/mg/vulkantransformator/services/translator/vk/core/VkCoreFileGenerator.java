package cz.mg.vulkantransformator.services.translator.vk.core;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;
import cz.mg.file.File;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.translator.vk.VkFileGenerator;
import cz.mg.vulkantransformator.services.translator.vk.component.VkComponentFileGenerator;

import java.nio.file.Path;

public @Service class VkCoreFileGenerator implements VkFileGenerator {
    private static volatile @Service VkCoreFileGenerator instance;

    public static @Service VkCoreFileGenerator getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VkCoreFileGenerator();
                    instance.configuration = VkCoreConfiguration.getInstance();
                    instance.vkComponentFileGenerator = VkComponentFileGenerator.getInstance();
                    instance.vulkanExceptionGenerator = VulkanExceptionGenerator.getInstance();
                    instance.joiner = StringJoiner.getInstance();
                }
            }
        }
        return instance;
    }

    private @Service VkCoreConfiguration configuration;
    private @Service VkComponentFileGenerator vkComponentFileGenerator;
    private @Service VulkanExceptionGenerator vulkanExceptionGenerator;
    private @Service StringJoiner joiner;

    private VkCoreFileGenerator() {
    }

    @Override
    public @Mandatory String getSourceFileName() {
        return configuration.getSourceFileName();
    }

    @Override
    public @Mandatory List<File> generateFiles(@Mandatory VkRoot root) {
        List<File> files = vkComponentFileGenerator.generateFiles(root, configuration);

        files.addLast(
            new File(
                Path.of(configuration.getDirectory(), vulkanExceptionGenerator.getName() + ".java"),
                join(vulkanExceptionGenerator.generateJava(configuration))
            )
        );
        
        return files;
    }

    private @Mandatory String join(@Mandatory List<String> lines) {
        return joiner.join(lines, "\n");
    }
}
