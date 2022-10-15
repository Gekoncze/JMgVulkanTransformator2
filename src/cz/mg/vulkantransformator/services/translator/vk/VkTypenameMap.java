package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.vulkantransformator.services.translator.TypenameMap;

public @Service class VkTypenameMap implements TypenameMap {
    private static @Optional VkTypenameMap instance;

    public static @Mandatory VkTypenameMap getInstance() {
        if (instance == null) {
            instance = new VkTypenameMap();
        }
        return instance;
    }

    private static final List<Pair<String, String>> LIST = new List<>(
        new Pair<>("VkMemoryRequirements2KHR", "VkMemoryRequirements2") // quick fix for typedef problem
    );

    private static final Map<String, String> MAP = new Map<>(LIST.count() * 2, LIST);

    private VkTypenameMap() {
    }

    @Override
    public @Mandatory Map<String, String> getMap() {
        return MAP;
    }
}
