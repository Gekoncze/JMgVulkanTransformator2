package cz.mg.vulkantransformator.services.translator.vk.core;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Service class VulkanExceptionGenerator {
    private static @Optional VulkanExceptionGenerator instance;

    public static @Mandatory VulkanExceptionGenerator getInstance() {
        if (instance == null) {
            instance = new VulkanExceptionGenerator();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private CodeGenerator codeGenerator;

    private VulkanExceptionGenerator() {
    }

    public @Mandatory String getName() {
        return "VulkanException";
    }

public @Mandatory List<String> generateJava(@Mandatory LibraryConfiguration configuration) {
        List<String> lines = codeGenerator.generateJavaHeading(configuration);

        lines.addCollectionLast(
            new List<>(
                "public class " + getName() + " extends RuntimeException {",
                "    private final int result;",
                "",
                "    public " + getName() + "(int result) {",
                "        this.result = result;",
                "    }",
                "",
                "    public int getResult() {",
                "        return result;",
                "    }",
                "}",
                ""
            )
        );

        return lines;
    }
}
