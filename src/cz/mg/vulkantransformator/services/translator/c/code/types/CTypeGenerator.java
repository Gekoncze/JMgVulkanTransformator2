package cz.mg.vulkantransformator.services.translator.c.code.types;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.c.CConfiguration;

public @Service class CTypeGenerator {
    private static @Optional CTypeGenerator instance;

    public static @Mandatory CTypeGenerator getInstance() {
        if (instance == null) {
            instance = new CTypeGenerator();
            instance.configuration = CConfiguration.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private CConfiguration configuration;
    private CodeGenerator codeGenerator;

    private CTypeGenerator() {
    }

    public @Mandatory List<String> generateJava(@Mandatory String className, @Mandatory String javaType) {
        return new List<>(
            "package " + configuration.getJavaPackage() + ";",
            "",
            "public class " + className + " extends CObject {",
            "    public static final long SIZE = _size();",
            "",
            "    public " + className + "(long address) {",
            "        super(address);",
            "    }",
            "",
            "    private static native long _size();",
            "",
            "    public " + javaType + " get() {",
            "        return _get(address);",
            "    }",
            "",
            "    private static native " + javaType + " _get(long address);",
            "",
            "    public void set(" + javaType + " value) {",
            "        _set(address, value);",
            "    }",
            "",
            "    private static native void _set(long address, " + javaType + " value);",
            "",
            "    public void set(" + className + " value) {",
            "        _set(address, value.get());",
            "    }",
            "}"
        );
    }

    public @Mandatory List<String> generateNative(
        @Mandatory String className,
        @Mandatory String jniType,
        @Mandatory String nativeType
    ) {
        JniFunction sizeFunction = new JniFunction();
        sizeFunction.setOutput("jlong");
        sizeFunction.setClassName(className);
        sizeFunction.setName("_size");
        sizeFunction.setLines(
            new List<>(
                "return sizeof(" + nativeType + ");"
            )
        );

        JniFunction getFunction = new JniFunction();
        getFunction.setOutput(jniType);
        getFunction.setClassName(className);
        getFunction.setName("_get");
        getFunction.setInput(
            new List<>(
                "jlong address"
            )
        );
        getFunction.setLines(
            new List<>(
                nativeType + "* a = (" + nativeType + "*) l2a(address);",
                "return *a;"
            )
        );

        JniFunction setFunction = new JniFunction();
        setFunction.setOutput("void");
        setFunction.setClassName(className);
        setFunction.setName("_set");
        setFunction.setInput(
            new List<>(
                "jlong address",
                jniType + " value"
            )
        );
        setFunction.setLines(
            new List<>(
                nativeType + "* a = (" + nativeType + "*) l2a(address);",
                "*a = value;"
            )
        );

        List<String> lines = codeGenerator.generateNativeHeading(configuration, null);
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, sizeFunction));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, getFunction));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, setFunction));
        return lines;
    }
}
