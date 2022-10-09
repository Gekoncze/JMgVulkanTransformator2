package cz.mg.vulkantransformator.services.translator.vk.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;

public @Service class VkComponentTranslator {
    private static @Optional VkComponentTranslator instance;

    public static @Mandatory VkComponentTranslator getInstance() {
        if (instance == null) {
            instance = new VkComponentTranslator();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private CodeGenerator codeGenerator;

    private VkComponentTranslator() {
    }

    public @Mandatory List<String> getCommonJavaHeader(
        @Mandatory VkComponent component,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = codeGenerator.generateJavaHeader(configuration);

        lines.addCollectionLast(
            new List<>(
                "public class " + component.getName() + " extends CObject {",
                "    public static final long SIZE = _size();",
                "",
                "    public " + component.getName() + "(long address) {",
                "        super(address);",
                "    }",
                "",
                "    private static native long _size();",
                "",
                "    public void set(" + component.getName() + " object) {",
                "        _set(object.address, address);",
                "    }",
                "",
                "    private static native void _set(long source, long destination);",
                ""
            )
        );

        return lines;
    }

    public @Mandatory List<String> getCommonJavaFooter(@Mandatory VkComponent component) {
        return new List<>("}");
    }

    public @Mandatory List<String> getCommonNativeHeader(
        @Mandatory VkComponent component,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = codeGenerator.generateNativeHeader(configuration);

        JniFunction sizeFunction = new JniFunction();
        sizeFunction.setOutput("jlong");
        sizeFunction.setClassName(component.getName());
        sizeFunction.setName("_size");
        sizeFunction.setLines(
            new List<>(
                "return sizeof(" + component.getName() + ");"
            )
        );

        JniFunction setFunction = new JniFunction();
        setFunction.setOutput("void");
        setFunction.setClassName(component.getName());
        setFunction.setName("_set");
        setFunction.setInput(
            new List<>(
                "jlong source",
                "jlong destination"
            )
        );
        setFunction.setLines(
            new List<>(
                "memcpy(l2a(destination), l2a(source), sizeof(" + component.getName() + "));"
            )
        );

        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, sizeFunction));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, setFunction));
        lines.addLast("");

        return lines;
    }

    public @Mandatory List<String> getCommonNativeFooter(@Mandatory VkComponent component) {
        return new List<>();
    }
}
