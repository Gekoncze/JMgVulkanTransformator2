package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;

public @Utility interface LibraryConfiguration {
    @Mandatory String getName();
    @Mandatory String getJavaLibraryName();
    @Mandatory String getNativeLibraryName();
    @Mandatory String getJavaPackage();

    default @Mandatory String getSubModulePrefix() {
        return "";
    }

    default @Mandatory String getSourceFileName() {
        throw new UnsupportedOperationException();
    }

    default @Mandatory String getDirectory() {
        return getJavaPackage().replace(".", "/");
    }

    default @Mandatory List<String> getJavaDependencies() {
        return new List<>();
    }

    default @Mandatory List<String> getNativeDependencies() {
        return new List<>();
    }

    default @Mandatory List<String> getLibraryDependencies() {
        return new List<>();
    }

    default @Mandatory List<TypenameMap> getTypenameMaps() {
        return new List<>();
    }
}
