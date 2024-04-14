package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkVariable;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.ObjectCodeGenerator;
import cz.mg.vulkantransformator.services.translator.vk.Index;

public @Service class VkFunctionTranslator implements VkTranslator<VkFunction> {
    private static final @Mandatory String OUTPUT_NAME = "result";

    private static @Optional VkFunctionTranslator instance;

    public static @Mandatory VkFunctionTranslator getInstance() {
        if (instance == null) {
            instance = new VkFunctionTranslator();
            instance.fieldTranslator = VkFieldTranslator.getInstance();
            instance.objectCodeGenerator = ObjectCodeGenerator.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
            instance.stringJoiner = StringJoiner.getInstance();
        }
        return instance;
    }

    private VkFieldTranslator fieldTranslator;
    private ObjectCodeGenerator objectCodeGenerator;
    private CodeGenerator codeGenerator;
    private StringJoiner stringJoiner;

    private VkFunctionTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkFunction.class;
    }

    @Override
    public @Mandatory String getJavaName(@Mandatory VkFunction function) {
        return function.getName().replaceFirst("vk", "Vk");
    }

    @Override
    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkFunction function,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        lines.addCollectionLast(
            objectCodeGenerator.getCommonJavaHeading(getJavaName(function), configuration)
        );

        lines.addCollectionLast(
            new List<>(
                "    public void call() {",
                "        _call(address);",
                "    }",
                "",
                "    private static native void _call(long address);",
                ""
            )
        );

        if (isResult(function.getOutput())) {
            lines.addCollectionLast(
                new List<>(
                    "    public void validate() {",
                    "        int result = getResult().get();",
                    "        if (result != VkResult.VK_SUCCESS_I) {",
                    "            throw new VulkanException(result);",
                    "        }",
                    "    }",
                    ""
                )
            );
        }

        for (VkVariable field : function.getInput()) {
            lines.addCollectionLast(
                fieldTranslator.translateJava(getJavaName(function), field, configuration)
            );
        }

        if (!isVoid(function.getOutput())) {
            lines.addCollectionLast(
                fieldTranslator.translateJava(
                    getJavaName(function),
                    convertOutput(function.getOutput()),
                    configuration
                )
            );
        }

        codeGenerator.removeLastEmptyLine(lines);

        lines.addCollectionLast(
            objectCodeGenerator.getCommonJavaFooter()
        );

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory VkFunction function,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        String dependency = "#include " + '"' + getJavaName(function) + ".h" + '"';

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeHeading(
                getJavaName(function),
                getJavaName(function),
                dependency,
                configuration
            )
        );

        JniFunction callFunction = new JniFunction();
        callFunction.setStatic(true);
        callFunction.setClassName(getJavaName(function));
        callFunction.setOutput("void");
        callFunction.setName("_call");
        callFunction.setInput(new List<>("jlong address"));
        callFunction.setLines(
            new List<>(
                getJavaName(function) + "* pc = l2a(address);",
                generateCallPrefix(function) + function.getName() + "(" + generateCallArguments(function) + ");"
            )
        );

        lines.addCollectionLast(codeGenerator.generateJniFunction(configuration, callFunction));

        for (VkVariable field : function.getInput()) {
            lines.addCollectionLast(
                fieldTranslator.translateNative(getJavaName(function), field, configuration)
            );
        }

        if (!isVoid(function.getOutput())) {
            lines.addCollectionLast(
                fieldTranslator.translateNative(getJavaName(function), convertOutput(function.getOutput()), configuration)
            );
        }

        lines.addCollectionLast(
            objectCodeGenerator.getCommonNativeFooter()
        );

        return lines;
    }

    private @Mandatory String generateCallArguments(@Mandatory VkFunction function) {
        List<String> arguments = new List<>();
        for (VkVariable argument : function.getInput()) {
            arguments.addLast("pc->" + argument.getName());
        }
        return stringJoiner.join(arguments, ", ");
    }

    private @Mandatory String generateCallPrefix(@Mandatory VkFunction function) {
        if (!isVoid(function.getOutput())) {
            return "pc->" + OUTPUT_NAME + " = ";
        } else {
            return "";
        }
    }

    @Override
    public @Mandatory List<String> translateNativeHeader(
        @Mandatory Index index,
        @Mandatory VkFunction function,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = codeGenerator.generateNativeHeading(configuration, null);

        lines.addLast("typedef struct {");

        for (VkVariable field : function.getInput()) {
            lines.addLast("    " + getNativeDeclaration(function, field, null) + ";");
        }

        if (!isVoid(function.getOutput())) {
            lines.addLast("    " + getNativeDeclaration(function, function.getOutput(), OUTPUT_NAME) + ";");
        }

        lines.addLast("} " + getJavaName(function) + ";");
        lines.addLast("");

        return codeGenerator.addHeaderFileGuards(lines, "JMGVK_" + getJavaName(function).toUpperCase() + "_H");
    }

    private boolean isVoid(@Mandatory VkVariable variable) {
        return variable.getTypename().equals("void")
            && variable.getPointers().isEmpty()
            && variable.getArrays().isEmpty();
    }

    private boolean isResult(@Mandatory VkVariable variable) {
        return variable.getTypename().equals("VkResult")
            && variable.getPointers().isEmpty()
            && variable.getArrays().isEmpty();
    }

    private @Mandatory VkVariable convertOutput(@Mandatory VkVariable output) {
        return new VkVariable(
            output.getTypename(),
            OUTPUT_NAME,
            new List<>(output.getPointers()),
            new List<>(output.getArrays())
        );
    }

    private @Mandatory String getNativeDeclaration(
        @Mandatory VkFunction function,
        @Mandatory VkVariable variable,
        @Optional String otherName
    ) {
        String type = getNativeType(function, variable);
        String name = otherName != null ? otherName : variable.getName();
        String postfix = getNativeTypeArray(function, variable);
        return type + " " + name + postfix;
    }

    private @Mandatory String getNativeType(@Mandatory VkFunction function, @Mandatory VkVariable variable) {
        // TODO - implement unsupported cases
        if (!variable.getPointers().isEmpty() && !variable.getArrays().isEmpty()) {
            throw new UnsupportedOperationException(getErrorArrayPointer(function, variable));
        } else if (!variable.getPointers().isEmpty()) {
            return variable.getTypename() + "*".repeat(variable.getPointers().count());
        } else if (!variable.getArrays().isEmpty()) {
            if (variable.getArrays().getFirst().getCount() == 1) {
                return variable.getTypename();
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return variable.getTypename();
        }
    }

    private @Mandatory String getNativeTypeArray(@Mandatory VkFunction function, @Mandatory VkVariable variable) {
        // TODO - implement unsupported cases
        if (!variable.getPointers().isEmpty() && !variable.getArrays().isEmpty()) {
            throw new UnsupportedOperationException(getErrorArrayPointer(function, variable));
        } else if (!variable.getPointers().isEmpty()) {
            return "";
        } else if (!variable.getArrays().isEmpty()) {
            if (variable.getArrays().getFirst().getCount() == 1) {
                return "[" + variable.getArrays().getFirst().getCount() + "]";
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            return "";
        }
    }

    private @Mandatory String getErrorArrayPointer(@Mandatory VkFunction function, @Mandatory VkVariable variable) {
        return "Unsupported combination of function parameter pointer and array count: " +
            "component = " + function.getName() + " " +
            "parameter = " + variable.getName() + " " +
            "pointers = " + variable.getPointers().count() + " " +
            "array = " + variable.getArrays().count();
    }
}
