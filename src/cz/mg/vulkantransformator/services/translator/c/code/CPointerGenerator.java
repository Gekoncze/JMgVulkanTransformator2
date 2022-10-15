package cz.mg.vulkantransformator.services.translator.c.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.c.CConfiguration;

public @Service class CPointerGenerator implements CGenerator {
    private static @Optional CPointerGenerator instance;

    public static @Mandatory CPointerGenerator getInstance() {
        if (instance == null) {
            instance = new CPointerGenerator();
            instance.factoryGenerator = CFactoryGenerator.getInstance();
            instance.configuration = CConfiguration.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private CFactoryGenerator factoryGenerator;
    private CConfiguration configuration;
    private CodeGenerator codeGenerator;

    private CPointerGenerator() {
    }

    @Override
    public @Mandatory String getName() {
        return "CPointer";
    }

    @Override
    public @Mandatory List<String> generateJava() {
        String genericFactoryName = factoryGenerator.getName() + "<T>";
        return new List<>(
            "package " + configuration.getJavaPackage() + ";",
            "",
            "public class " + getName() + "<T extends CObject> extends CObject {",
            "    public static final long SIZE = _size();",
            "",
            "    private final long size;",
            "    private final " + genericFactoryName + " factory;",
            "",
            "    public " + getName() + "(long address, long size, " + genericFactoryName + " factory) {",
            "        super(address);",
            "        this.size = size;",
            "        this.factory = factory;",
            "    }",
            "",
            "    private static native long _size();",
            "",
            "    public T getTarget() {",
            "        return create(get());",
            "    }",
            "",
            "    public T getTarget(int i) {",
            "        return create(offset(get(), i, size));",
            "    }",
            "",
            "    public void setTarget(T target) {",
            "        set(target == null ? CMemory.NULL : target.getAddress());",
            "    }",
            "",
            "    public long get() {",
            "        return _get(address);",
            "    }",
            "",
            "    private static native long _get(long address);",
            "",
            "    public void set(long value) {",
            "        _set(address, value);",
            "    }",
            "",
            "    private static native void _set(long address, long value);",
            "",
            "    public static native long offset(long address, int i, long size);",
            "",
            "    private T create(long targetAddress) {",
            "        if (targetAddress != CMemory.NULL) {",
            "            return factory.create(targetAddress);",
            "        } else {",
            "            return null;",
            "        }",
            "    }",
            "}"
        );
    }

    @Override
    public @Mandatory List<String> generateNativeC() {
        JniFunction sizeFunction = new JniFunction();
        sizeFunction.setOutput("jlong");
        sizeFunction.setClassName(getName());
        sizeFunction.setName("_size");
        sizeFunction.setLines(
            new List<>(
                "return sizeof(void*);"
            )
        );

        JniFunction getFunction = new JniFunction();
        getFunction.setOutput("jlong");
        getFunction.setClassName(getName());
        getFunction.setName("_get");
        getFunction.setInput(
            new List<>(
                "jlong address"
            )
        );
        getFunction.setLines(
            new List<>(
                "void** a = (void**) l2a(address);",
                "return a2l(*a);"
            )
        );

        JniFunction setFunction = new JniFunction();
        setFunction.setOutput("void");
        setFunction.setClassName(getName());
        setFunction.setName("_set");
        setFunction.setInput(
            new List<>(
                "jlong address",
                "jlong value"
            )
        );
        setFunction.setLines(
            new List<>(
                "void** a = (void**) l2a(address);",
                "*a = l2a(value);"
            )
        );

        JniFunction offsetFunction = new JniFunction();
        offsetFunction.setOutput("jlong");
        offsetFunction.setClassName(getName());
        offsetFunction.setName("offset");
        offsetFunction.setInput(
            new List<>(
                "jlong address",
                "jint i",
                "jlong size"
            )
        );
        offsetFunction.setLines(
            new List<>(
                "void* a = (void*) l2a(address);",
                "return a2l(a + i * size);"
            )
        );

        List<String> lines = codeGenerator.generateNativeHeader(configuration);
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, sizeFunction));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, getFunction));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, setFunction));
        lines.addLast("");
        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, offsetFunction));
        return lines;
    }

    @Override
    public @Mandatory List<String> generateNativeH() {
        return new List<>();
    }
}
