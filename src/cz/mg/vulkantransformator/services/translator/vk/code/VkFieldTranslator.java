package cz.mg.vulkantransformator.services.translator.vk.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.translator.JniFunction;
import cz.mg.vulkantransformator.entities.vulkan.VkComponent;
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
        @Mandatory VkComponent component,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        String offsetFieldName = getOffsetFieldName(field);
        String offsetMethodName = getOffsetMethodName(field);
        List<String> lines = new List<>();
        lines.addLast("    private static final long " + offsetFieldName + " = " + offsetMethodName + "();");
        lines.addLast("");
        lines.addCollectionLast(translateJavaGetter(component, field, configuration));
        lines.addLast("");
        lines.addLast("    private static native long " + offsetMethodName + "();");
        return lines;
    }

    private @Mandatory List<String> translateJavaGetter(
        @Mandatory VkComponent component,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        if (field.getArray() > 0 && field.getPointers() > 0) {
            throw new UnsupportedOperationException(
                "Unsupported pointer and array options for '" + getFullName(component, field) + "': "
                    + field.getPointers() + ", " + field.getArray() + "."
            );
        }

        if (field.getTypename().equals("void") && field.getPointers() < 1) {
            throw new IllegalArgumentException(
                "Invalid void field for '" + getFullName(component, field) + "'."
            );
        }

        if (field.getPointers() == 0 && field.getArray() == 0) {
            return translateJavaGetterValue(component, field, configuration);
        } else if (field.getPointers() > 0) {
            return translateJavaGetterPointer(component, field, configuration);
        } else if (field.getArray() > 0) {
            return translateJavaGetterArray(component, field, configuration);
        } else {
            throw new UnsupportedOperationException(
                "Unsupported pointer and array options for '" + getFullName(component, field) + "': "
                    + field.getPointers() + ", " + field.getArray() + "."
            );
        }
    }

    private @Mandatory List<String> translateJavaGetterValue(
        @Mandatory VkComponent component,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        String type = getTypename(field, configuration);
        String methodName = getMethodName(field);
        String offsetFieldName = getOffsetFieldName(field);
        return new List<>(
            "    public " + type + " " + methodName + "() {",
            "        return new " + type + "(address + " + offsetFieldName + ");",
            "    }"
        );
    }

    private @Mandatory List<String> translateJavaGetterPointer(
        @Mandatory VkComponent component,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        if (field.getPointers() == 1) {
            return translateJavaGetterPointer1D(component, field, configuration);
        } else if (field.getPointers() == 2) {
            return translateJavaGetterPointer2D(component, field, configuration);
        } else {
            String target = component.getName() + "." + field.getName();
            throw new UnsupportedOperationException(
                "Unsupported pointer count for '" + target + "': " + field.getPointers() + "."
            );
        }
    }

    private @Mandatory List<String> translateJavaGetterPointer1D(
        @Mandatory VkComponent component,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        String type = "CPointer<" + getTypename(field, configuration) + ">";
        return new List<>(
            "    public " + type + " " + getMethodName(field) + "() {",
            "        return new CPointer<>(",
            "             " + getAddressArgument(field) + ",",
            "             " + getTypename(field, configuration) + ".SIZE,",
            "             " + getTypename(field, configuration) + "::new",
            "        );",
            "    }"
        );
    }

    private @Mandatory List<String> translateJavaGetterPointer2D(
        @Mandatory VkComponent component,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        String type = "CPointer<CPointer<" + getTypename(field, configuration) + ">>";
        return new List<>(
            "    public " + type + " " + getMethodName(field) + "() {",
            "        return new CPointer<>(",
            "             " + getAddressArgument(field) + ",",
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
        @Mandatory VkComponent component,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        String type = "CArray<" + getTypename(field, configuration) + ">";
        return new List<>(
            "    public " + type + " " + getMethodName(field) + "() {",
            "        return new CArray<>(",
                "            " + getAddressArgument(field) + ",",
                "            " + field.getArray() + ",",
                "            " + getTypename(field, configuration) + ".SIZE,",
                "            " + getTypename(field, configuration) + "::new",
            "        );",
            "    }"
        );
    }

    public @Mandatory List<String> translateNative(
        @Mandatory VkComponent component,
        @Mandatory VkVariable field,
        @Mandatory LibraryConfiguration configuration
    ) {
        JniFunction function = new JniFunction();
        function.setOutput("jlong");
        function.setClassName(component.getName());
        function.setName(getOffsetMethodName(field));
        function.setLines(
            new List<>(
                component.getName() + " component;",
                "jlong address = a2l(&component);",
                "jlong fieldAddress = a2l(&(component." + field.getName() + "));",
                "return fieldAddress - address;"
            )
        );
        return codeGenerator.generateJniFunction(configuration, function);
    }

    private @Mandatory String capitalizeFirst(@Mandatory String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    private @Mandatory String getFullName(@Mandatory VkComponent component, @Mandatory VkVariable field) {
        return component.getName() + "." + field.getName();
    }

    private @Mandatory String getMethodName(@Mandatory VkVariable field) {
        return "get" + capitalizeFirst(field.getName());
    }

    private @Mandatory String getOffsetFieldName(@Mandatory VkVariable field) {
        return field.getName().toUpperCase() + "_OFFSET";
    }

    private @Mandatory String getOffsetMethodName(@Mandatory VkVariable field) {
        return "_get" + capitalizeFirst(field.getName()) + "Offset";
    }

    private @Mandatory String getAddressArgument(@Mandatory VkVariable field) {
        return "address + " + getOffsetFieldName(field);
    }

    private @Mandatory String getTypename(@Mandatory VkVariable field, @Mandatory LibraryConfiguration configuration) {
        return typenameTranslator.translate(field.getTypename(), configuration);
    }
}
