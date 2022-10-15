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
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameTranslator;

public @Service class VkFunctionTranslator implements VkTranslator<VkFunction> {
    private static @Optional VkFunctionTranslator instance;

    public static @Mandatory VkFunctionTranslator getInstance() {
        if (instance == null) {
            instance = new VkFunctionTranslator();
            instance.typenameTranslator = TypenameTranslator.getInstance();
            instance.joiner = StringJoiner.getInstance();
            instance.functionsTranslator = VkFunctionsTranslator.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private TypenameTranslator typenameTranslator;
    private StringJoiner joiner;
    private VkFunctionsTranslator functionsTranslator;
    private CodeGenerator codeGenerator;

    private VkFunctionTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkFunction.class;
    }

    @Override
    public @Mandatory List<String> translateJava(
        @Mandatory Index index,
        @Mandatory VkFunction function,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();

        List<String> parameterList = new List<>();
        List<String> argumentList = new List<>();
        List<String> nativeParameterList = new List<>();

        for (VkVariable parameter : function.getInput()) {
            parameterList.addLast(getType(parameter, configuration) + " " + parameter.getName());
            argumentList.addLast(parameter.getName() + ".getAddress()");
            nativeParameterList.addLast("long " + parameter.getName());
        }

        if (!isVoid(function.getOutput())) {
            parameterList.addLast(getType(function.getOutput(), configuration) + " output");
            argumentList.addLast("output.getAddress()");
            nativeParameterList.addLast("long output");
        }

        String parameters = joiner.join(parameterList, ", ");
        String arguments = joiner.join(argumentList, ", ");
        String nativeParameters = joiner.join(nativeParameterList, ", ");

        lines.addCollectionLast(new List<>(
            "    public void " + function.getName() + "(" + parameters + ") {",
            "        _" + function.getName() + "(" + arguments + ");",
            "    }",
            "",
            "    private static native void _" + function.getName() + "(" + nativeParameters + ");"
        ));

        return lines;
    }

    @Override
    public @Mandatory List<String> translateNative(
        @Mandatory Index index,
        @Mandatory VkFunction function,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> parameters = new List<>();
        List<String> variables = new List<>();
        List<String> arguments = new List<>();

        for (VkVariable parameter : function.getInput()) {
            parameters.addLast("jlong " + parameter.getName());
            variables.addLast("    " + getTypeC(parameter) + "* _" + parameter.getName() + " = l2a(" + parameter.getName() + ");");
            arguments.addLast("*_" + parameter.getName());
        }

        if (!isVoid(function.getOutput())) {
            parameters.addLast("jlong output");
            variables.addLast("    " + getTypeC(function.getOutput()) + "* _output = l2a(output);");
        }

        JniFunction jniFunction = new JniFunction();
        jniFunction.setOutput("void");
        jniFunction.setClassName(functionsTranslator.getName(configuration));
        jniFunction.setName(function.getName());
        jniFunction.setInput(parameters);

        List<String> lines = new List<>();

        lines.addCollectionLast(variables);

        String expressionPrefix = isVoid(function.getOutput()) ? "" : "*_output = ";
        String argumentsLine = joiner.join(arguments, ", ");
        lines.addCollectionLast(new List<>(
            "    " + expressionPrefix + function.getName() + "(" + argumentsLine + ");"
        ));

        jniFunction.setLines(lines);

        return codeGenerator.generateJniFunction(configuration, jniFunction);
    }

    private @Mandatory String getType(@Mandatory VkVariable variable, @Mandatory LibraryConfiguration configuration) {
        String typename = typenameTranslator.translate(variable.getTypename(), configuration);

        if (variable.getPointers() == 0 && variable.getArray() == 0) {
            return typename;
        } else if (variable.getPointers() > 0 && variable.getArray() > 0) {
            throw new UnsupportedOperationException(
                "Unsupported combination of pointers and array for variable " + variable.getName() + "."
            );
        } else if (variable.getPointers() < 0 || variable.getArray() < 0) {
            throw new IllegalArgumentException(
                "Wrong value of pointers or array for variable " + variable.getName() + "."
            );
        } else if (variable.getPointers() == 1) {
            return "CPointer<" + typename + ">";
        } else if (variable.getPointers() == 2) {
            return "CPointer<CPointer<" + typename + ">>";
        } else if (variable.getArray() > 0) {
            return "CArray<" + typename + ">";
        } else {
            throw new UnsupportedOperationException(
                "Unsupported pointers or array for variable " + variable.getName() + "."
            );
        }
    }

    private @Mandatory String getTypeC(@Mandatory VkVariable variable) {
        String typename = variable.getTypename();

        if (variable.getPointers() == 0 && variable.getArray() == 0) {
            return typename;
        } else if (variable.getPointers() > 0 && variable.getArray() > 0) {
            throw new UnsupportedOperationException(
                "Unsupported combination of pointers and array for variable " + variable.getName() + "."
            );
        } else if (variable.getPointers() < 0 || variable.getArray() < 0) {
            throw new IllegalArgumentException(
                "Wrong value of pointers or array for variable " + variable.getName() + "."
            );
        } else if (variable.getPointers() == 1) {
            return typename + "*";
        } else if (variable.getPointers() == 2) {
            return typename + "**";
        } else if (variable.getArray() > 0) {
            return typename + "*";
        } else {
            throw new UnsupportedOperationException(
                "Unsupported pointers or array for variable " + variable.getName() + "."
            );
        }
    }

    private boolean isVoid(@Mandatory VkVariable variable) {
        return variable.getTypename().equals("void") && variable.getPointers() == 0;
    }
}
