package cz.mg.vulkantransformator.services.translator.vk.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkEnum;
import cz.mg.vulkantransformator.entities.vulkan.VkEnumEntry;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.VkLibraryConfiguration;

public @Service class VkEnumEntryTranslator {
    private static @Optional VkEnumEntryTranslator instance;

    public static @Mandatory VkEnumEntryTranslator getInstance() {
        if (instance == null) {
            instance = new VkEnumEntryTranslator();
            instance.configuration = VkLibraryConfiguration.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private VkLibraryConfiguration configuration;
    private CodeGenerator codeGenerator;

    private VkEnumEntryTranslator() {
    }

    public @Mandatory List<String> translateJava(@Mandatory VkEnum enumeration, @Mandatory VkEnumEntry entry) {
        String type = enumeration.getName();
        return new List<>(
            "    public static final " + type + " " + entry.getName() + " = new "+ type + "(",
            "         get_" + entry.getName() + "()",
            "    );",
            "",
            "    public static final int " + entry.getName() + "_I" + " = " + entry.getName() + ".get();",
            "",
            "    private static native long get_" + entry.getName() + "();",
            ""
        );
    }

    public @Mandatory List<String> translateNative(@Mandatory VkEnum enumeration, @Mandatory VkEnumEntry entry) {
        JniFunction getFunction = new JniFunction();
        getFunction.setOutput("jlong");
        getFunction.setClassName(enumeration.getName());
        getFunction.setName("get_" + entry.getName());
        getFunction.setLines(
            new List<>(
                "return a2l(&_" + entry.getName() + ");"
            )
        );

        List<String> lines = new List<>();
        lines.addLast(enumeration.getName() + " _" + entry.getName() + " = " + entry.getName() + ";");
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, getFunction));
        lines.addLast("");
        return lines;
    }
}
