package cz.mg.vulkantransformator.services.translator.c;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.translator.LibraryConfiguration;
import cz.mg.vulkantransformator.services.translator.TypenameMap;

public @Utility class CConfiguration implements LibraryConfiguration {
    private static @Optional CConfiguration instance;

    public static @Mandatory CConfiguration getInstance() {
        if (instance == null) {
            instance = new CConfiguration();
            instance.cTypenameMap = CTypenameMap.getInstance();
        }
        return instance;
    }

    private CTypenameMap cTypenameMap;

    private CConfiguration() {
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
