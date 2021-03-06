package cz.mg.vulkantransformator.services.translator.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;

public @Service interface Generator {
    boolean isVulkan();
    @Mandatory String getName();
    @Mandatory List<String> generateJava();
    @Mandatory List<String> generateNativeC();
    @Mandatory List<String> generateNativeH();
}
