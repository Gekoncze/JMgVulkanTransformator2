package cz.mg.vulkantransformator.services.translator.generators;

import cz.mg.annotations.classes.Service;
import cz.mg.collections.list.List;

public @Service interface VkGenerator {
    String getName();
    List<String> generateJava();
    List<String> generateNativeC();
    List<String> generateNativeH();
}
