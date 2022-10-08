package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.services.StringJoiner;
import cz.mg.vulkantransformator.entities.translator.JniFunction;

public @Service class CodeGenerator {
    private static @Optional CodeGenerator instance;

    public static @Mandatory CodeGenerator getInstance() {
        if (instance == null) {
            instance = new CodeGenerator();
            instance.stringJoiner = StringJoiner.getInstance();
        }
        return instance;
    }

    private StringJoiner stringJoiner;

    private CodeGenerator() {
    }

    public @Mandatory List<String> generateJavaHeader(@Mandatory LibraryConfiguration configuration) {
        List<String> lines = new List<>();

        lines.addLast("package " + configuration.getJavaPackage() + ";");
        lines.addLast("");

        if (!configuration.getJavaDependencies().isEmpty()) {
            lines.addCollectionLast(configuration.getJavaDependencies());
            lines.addLast("");
        }

        return lines;
    }

    public @Mandatory List<String> generateNativeHeader(@Mandatory LibraryConfiguration configuration) {
        List<String> lines = new List<>();

        if (!configuration.getNativeDependencies().isEmpty()) {
            lines.addCollectionLast(configuration.getNativeDependencies());
            lines.addLast("");
        }

        return lines;
    }

    public @Mandatory List<String> generateJniFunction(
        @Mandatory LibraryConfiguration configuration,
        @Mandatory JniFunction function
    ) {
        String functionParameters = generateJniFunctionParameters(function);
        String functionName = generateJniFunctionName(configuration, function);
        List<String> functionLines = new List<>();
        functionLines.addLast("JNIEXPORT " + function.getOutput() + " JNICALL " + functionName + "(" + functionParameters + ") {");
        for (String line : function.getLines()) {
            functionLines.addLast("    " + line);
        }
        functionLines.addLast("}");
        return functionLines;
    }

    private @Mandatory String generateJniFunctionName(
        @Mandatory LibraryConfiguration configuration,
        @Mandatory JniFunction function
    ) {
        return "Java_" + configuration.getJavaPackage().replace(".", "_") + "_" + function.getClassName() + "_" + function.getName();
    }

    private @Mandatory String generateJniFunctionParameters(@Mandatory JniFunction function) {
        List<String> inputParameters = new List<>("JNIEnv* env", "jclass clazz");
        inputParameters.addCollectionLast(function.getInput());
        return stringJoiner.join(inputParameters, ", ");
    }

    public @Mandatory List<String> generateJavaLibraryClass(
        @Mandatory LibraryConfiguration configuration,
        @Mandatory String name
    ) {
        List<String> lines = generateJavaHeader(configuration);

        lines.addCollectionLast(
            new List<>(
                "public class " + name + " {",
                "    public static final String NAME = \"" + configuration.getLibraryName() + "\";",
                "",
                "    public static void load() {",
                "        System.loadLibrary(NAME);",
                "    }",
                "}"
            )
        );

        return lines;
    }

    public void removeLastEmptyLine(@Mandatory List<String> lines) {
        if (!lines.isEmpty()) {
            if (lines.getLast().isEmpty()) {
                lines.removeLast();
            }
        }
    }
}