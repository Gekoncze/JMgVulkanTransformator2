package cz.mg.vulkantransformator.services.translator.vk.component;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkVariable;
import cz.mg.vulkantransformator.services.translator.CodeGenerator;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameTranslator;

public @Service class VkFieldTranslator {
    private static @Optional VkFieldTranslator instance;

    public static @Mandatory VkFieldTranslator getInstance() {
        if (instance == null) {
            instance = new VkFieldTranslator();
            instance.typenameTranslator = TypenameTranslator.getInstance();
            instance.codeGenerator = CodeGenerator.getInstance();
        }
        return instance;
    }

    private TypenameTranslator typenameTranslator;
    private CodeGenerator codeGenerator;

    private VkFieldTranslator() {
    }

    public @Mandatory List<String> translateJava(
        @Mandatory String componentName,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        List<String> lines = new List<>();
        lines.addCollectionLast(translateJavaGetter(componentName, field, configuration));
        lines.addLast("");
        lines.addLast("    private static native long " + getFieldAddressMethodName(field) + "(long address);");
        lines.addLast("");
        return lines;
    }

    private @Mandatory List<String> translateJavaGetter(
        @Mandatory String componentName,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        if (field.getArray() > 0 && field.getPointers() > 0) {
            throw new UnsupportedOperationException(
                "Unsupported pointer and array options for '" + getFullName(componentName, field) + "': "
                    + field.getPointers() + ", " + field.getArray() + "."
            );
        }

        if (field.getTypename().equals("void") && field.getPointers() < 1) {
            throw new IllegalArgumentException(
                "Invalid void field for '" + getFullName(componentName, field) + "'."
            );
        }

        if (field.getPointers() == 0 && field.getArray() == 0) {
            return translateJavaGetterValue(field, configuration);
        } else if (field.getPointers() > 0) {
            return translateJavaGetterPointer(componentName, field, configuration);
        } else if (field.getArray() > 0) {
            return translateJavaGetterArray(field, configuration);
        } else {
            throw new UnsupportedOperationException(
                "Unsupported pointer and array options for '" + getFullName(componentName, field) + "': "
                    + field.getPointers() + ", " + field.getArray() + "."
            );
        }
    }

    private @Mandatory List<String> translateJavaGetterValue(
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        String type = getTypename(field, configuration);
        String name = getFieldMethodName(field);
        String argument = getFieldAddressArgument(field);
        return new List<>(
            "    public " + type + " " + name + "() {",
            "        return new " + type + "(" + argument + ");",
            "    }"
        );
    }

    private @Mandatory List<String> translateJavaGetterPointer(
        @Mandatory String componentName,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        if (field.getPointers() == 1) {
            return translateJavaGetterPointer1D(field, configuration);
        } else if (field.getPointers() == 2) {
            return translateJavaGetterPointer2D(field, configuration);
        } else {
            String target = componentName + "." + field.getName();
            throw new UnsupportedOperationException(
                "Unsupported pointer count for '" + target + "': " + field.getPointers() + "."
            );
        }
    }

    private @Mandatory List<String> translateJavaGetterPointer1D(
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        String type = "CPointer<" + getTypename(field, configuration) + ">";
        return new List<>(
            "    public " + type + " " + getFieldMethodName(field) + "() {",
            "        return new CPointer<>(",
            "             " + getFieldAddressArgument(field) + ",",
            "             " + getTypename(field, configuration) + ".SIZE,",
            "             " + getTypename(field, configuration) + "::new",
            "        );",
            "    }"
        );
    }

    private @Mandatory List<String> translateJavaGetterPointer2D(
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        String type = "CPointer<CPointer<" + getTypename(field, configuration) + ">>";
        return new List<>(
            "    public " + type + " " + getFieldMethodName(field) + "() {",
            "        return new CPointer<>(",
            "             " + getFieldAddressArgument(field) + ",",
            "             CPointer.SIZE,",
            "             (a) -> new CPointer<>(",
            "                 a,",
            "                 " + getTypename(field, configuration) + ".SIZE,",
            "                 " + getTypename(field, configuration) + "::new",
            "             )",
            "        );",
            "    }"
        );
    }

    private @Mandatory List<String> translateJavaGetterArray(
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        String type = "CArray<" + getTypename(field, configuration) + ">";
        return new List<>(
            "    public " + type + " " + getFieldMethodName(field) + "() {",
            "        return new CArray<>(",
                "            " + getFieldAddressArgument(field) + ",",
                "            " + field.getArray() + ",",
                "            " + getTypename(field, configuration) + ".SIZE,",
                "            " + getTypename(field, configuration) + "::new",
            "        );",
            "    }"
        );
    }

    public @Mandatory List<String> translateNative(
        @Mandatory String componentName,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        JniFunction function = new JniFunction();
        function.setStatic(true);
        function.setOutput("jlong");
        function.setClassName(componentName);
        function.setName(getFieldAddressMethodName(field));
        function.setInput(new List<>("jlong address"));
        function.setLines(
            new List<>(
                componentName + "* component = l2a(address);",
                "return a2l(&(component->" + field.getName() + "));"
            )
        );
        return codeGenerator.generateJniFunction(configuration, function);
    }

    private @Mandatory String capitalizeFirst(@Mandatory String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    private @Mandatory String getFullName(@Mandatory String componentName, @Mandatory VkVariable field) {
        return componentName + "." + field.getName();
    }

    private @Mandatory String getFieldMethodName(@Mandatory VkVariable field) {
        return "get" + capitalizeFirst(field.getName());
    }

    private @Mandatory String getFieldAddressMethodName(@Mandatory VkVariable field) {
        return "_get" + capitalizeFirst(field.getName()) + "Address";
    }

    private @Mandatory String getFieldAddressArgument(@Mandatory VkVariable field) {
        return getFieldAddressMethodName(field) + "(address)";
    }

    private @Mandatory String getTypename(@Mandatory VkVariable field, @Mandatory LibraryConfiguration configuration) {
        return typenameTranslator.translate(field.getTypename(), configuration);
    }
}
