package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
import cz.mg.vulkantransformator.entities.vulkan.VkFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkVariable;
import cz.mg.vulkantransformator.services.translator.Configuration;
import cz.mg.vulkantransformator.services.translator.Index;
import cz.mg.vulkantransformator.services.translator.c.CArrayGenerator;
import cz.mg.vulkantransformator.services.translator.c.CPointerGenerator;

public @Service class VkFunctionTranslator implements VkTranslator<VkFunction> {
    private static @Optional VkFunctionTranslator instance;

    public static @Mandatory VkFunctionTranslator getInstance() {
        if (instance == null) {
            instance = new VkFunctionTranslator();
            instance.typenameTranslator = TypenameTranslator.getInstance();
            instance.joiner = StringJoiner.getInstance();
            instance.functionsTranslator = VkFunctionsTranslator.getInstance();
            instance.pointerGenerator = CPointerGenerator.getInstance();
            instance.arrayGenerator = CArrayGenerator.getInstance();
        }
        return instance;
    }

    private TypenameTranslator typenameTranslator;
    private StringJoiner joiner;
    private VkFunctionsTranslator functionsTranslator;
    private CPointerGenerator pointerGenerator;
    private CArrayGenerator arrayGenerator;

    private VkFunctionTranslator() {
    }

    @Override
    public @Mandatory Class<? extends VkComponent> targetClass() {
        return VkFunction.class;
    }

    @Override
    public @Mandatory List<String> translateJava(@Mandatory Index index, @Mandatory VkFunction function) {
        List<String> lines = new List<>();

        List<String> parameterList = new List<>();
        List<String> argumentList = new List<>();
        List<String> nativeParameterList = new List<>();

        for (VkVariable parameter : function.getInput()) {
            parameterList.addLast(getType(parameter) + " " + parameter.getName());
            argumentList.addLast(parameter.getName() + ".getAddress()");
            nativeParameterList.addLast("long " + parameter.getName());
        }

        if (!isVoid(function.getOutput())) {
            parameterList.addLast(getType(function.getOutput()) + " output");
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
    public @Mandatory List<String> translateNative(@Mandatory Index index, @Mandatory VkFunction function) {
        List<String> lines = new List<>();

        List<String> parameterList = new List<>();
        List<String> variableList = new List<>();
        List<String> argumentList = new List<>();

        for (VkVariable parameter : function.getInput()) {
            parameterList.addLast("jlong " + parameter.getName());
            variableList.addLast("    " + getTypeC(parameter) + "* _" + parameter.getName() + " = l2a(" + parameter.getName() + ");");
            argumentList.addLast("*_" + parameter.getName());
        }

        if (!isVoid(function.getOutput())) {
            parameterList.addLast("jlong output");
            variableList.addLast("    " + getTypeC(function.getOutput()) + "* _output = l2a(output);");
        }

        String parametersPrefix = function.getInput().isEmpty() ? "" : ", ";
        String parameters = joiner.join(parameterList, ", ");
        String arguments = joiner.join(argumentList, ", ");

        String path = Configuration.VULKAN_FUNCTION + "_" + functionsTranslator.getName();
        String methodName = "_" + function.getName();
        String expressionPrefix = isVoid(function.getOutput()) ? "" : "*_output = ";

        lines.addCollectionLast(new List<>(
            "JNIEXPORT void JNICALL Java_" + path + "_" + methodName + "(JNIEnv* env, jclass clazz" + parametersPrefix + parameters + ") {"
        ));

        lines.addCollectionLast(variableList);

        lines.addCollectionLast(new List<>(
            "    " + expressionPrefix + function.getName() + "(" + arguments + ");",
            "}"
        ));

        return lines;
    }

    private @Mandatory String getType(@Mandatory VkVariable variable) {
        String typename = typenameTranslator.translate(variable.getTypename());

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
            return pointerGenerator.getName() + "<" + typename + ">";
        } else if (variable.getPointers() == 2) {
            return pointerGenerator.getName() + "<" + pointerGenerator.getName() + "<" + typename + ">>";
        } else if (variable.getArray() > 0) {
            return arrayGenerator.getName() + "<" + typename + ">";
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
