package cz.mg.vulkantransformator.services.translator.c;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameMap;

public @Utility class CLibraryConfiguration implements LibraryConfiguration {
    private static @Optional CLibraryConfiguration instance;

    public static @Mandatory CLibraryConfiguration getInstance() {
        if (instance == null) {
            instance = new CLibraryConfiguration();
            instance.cTypenameMap = CTypenameMap.getInstance();
        }
        return instance;
    }

    private CTypenameMap cTypenameMap;

    private CLibraryConfiguration() {
    }

    @Override
    public @Mandatory String getName() {
        return "C";
    }

    @Override
    public @Mandatory String getLibraryName() {
        return "jmgc";
    }

    @Override
    public @Mandatory String getJavaPackage() {
        return "cz.mg.c";
    }

    @Override
    public @Mandatory String getSubModulePrefix() {
        return "";
    }

    @Override
    public @Mandatory List<String> getNativeDependencies() {
        return new List<>(
            "#include \"CMemory.h\"",
            "#include <stdint.h>"
        );
    }

    @Override
    public @Mandatory List<TypenameMap> getTypenameMaps() {
        return new List<>(
            cTypenameMap
        );
    }
}
