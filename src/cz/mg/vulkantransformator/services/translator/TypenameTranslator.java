package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;

public @Service class TypenameTranslator {
    private static @Optional TypenameTranslator instance;

    public static @Mandatory TypenameTranslator getInstance() {
        if (instance == null) {
            instance = new TypenameTranslator();
        }
        return instance;
    }

    private TypenameTranslator() {
    }

    public @Mandatory String translate(
        @Mandatory String typename,
        @Mandatory LibraryConfiguration libraryConfiguration
    ) {
        for (TypenameMap typenameMap : libraryConfiguration.getTypenameMaps()) {
            String newTypename = typenameMap.getMap().getOptional(typename);
            if (newTypename != null) {
                return newTypename;
            }
        }

        return typename;
    }
}
