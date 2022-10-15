package cz.mg.vulkantransformator.services.translator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.map.Map;

public @Service interface TypenameMap {
    @Mandatory Map<String, String> getMap();
}
