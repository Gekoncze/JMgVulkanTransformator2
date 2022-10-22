package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;

public @Service class ObjectCodeGenerator {
    private static @Optional ObjectCodeGenerator instance;

    public static @Mandatory ObjectCodeGenerator getInstance() {
        if (instance == null) {
            instance = new ObjectCodeGenerator();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private CodeGenerator codeGenerator;

    private ObjectCodeGenerator() {
    }

    public @Mandatory List<String> getCommonJavaHeading(
        @Mandatory String name,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = codeGenerator.generateJavaHeading(configuration);

        lines.addCollectionLast(
            new List<>(
                "public class " + name + " extends CObject {",
                "    public static final long SIZE = _size();",
                "",
                "    public " + name + "(long address) {",
                "        super(address);",
                "    }",
                "",
                "    private static native long _size();",
                "",
                "    public void set(" + name + " object) {",
                "        _set(object.address, address);",
                "    }",
                "",
                "    private static native void _set(long source, long destination);",
                ""
            )
        );

        return lines;
    }

    public @Mandatory List<String> getCommonJavaFooter() {
        return new List<>("}");
    }

    public @Mandatory List<String> getCommonNativeHeading(
        @Mandatory String name,
        @Mandatory String nativeName,
        @Optional String additionalDependency,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = codeGenerator.generateNativeHeading(configuration, additionalDependency);

        JniFunction sizeFunction = new JniFunction();
        sizeFunction.setOutput("jlong");
        sizeFunction.setClassName(name);
        sizeFunction.setName("_size");
        sizeFunction.setLines(
            new List<>(
                "return sizeof(" + nativeName + ");"
            )
        );

        JniFunction setFunction = new JniFunction();
        setFunction.setOutput("void");
        setFunction.setClassName(name);
        setFunction.setName("_set");
        setFunction.setInput(
            new List<>(
                "jlong source",
                "jlong destination"
            )
        );
        setFunction.setLines(
            new List<>(
                "memcpy(l2a(destination), l2a(source), sizeof(" + nativeName + "));"
            )
        );

        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, sizeFunction));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, setFunction));
        lines.addLast("");

        return lines;
    }

    public @Mandatory List<String> getCommonNativeFooter() {
        return new List<>();
    }
}
