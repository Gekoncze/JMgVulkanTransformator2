package cz.mg.vulkantransformator.services.translator.c.code;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;

public @Service interface CGenerator {
    @Mandatory String getName();
    @Mandatory List<String> generateJava();
    @Mandatory List<String> generateNative();
    @Mandatory List<String> generateNativeHeader();
}
