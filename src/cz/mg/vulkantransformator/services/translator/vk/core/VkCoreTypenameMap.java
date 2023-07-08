package cz.mg.vulkantransformator.services.translator.vk.core;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.components.Capacity;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.vulkantransformator.services.translator.TypenameMap;

public @Service class VkCoreTypenameMap implements TypenameMap {
    private static @Optional VkCoreTypenameMap instance;

    public static @Mandatory VkCoreTypenameMap getInstance() {
        if (instance == null) {
            instance = new VkCoreTypenameMap();
        }
        return instance;
    }

    private static final List<Pair<String, String>> LIST = new List<>(
        new Pair<>("VkMemoryRequirements2KHR", "VkMemoryRequirements2") // quick fix for typedef problem
    );

    private static final Map<String, String> MAP = new Map<>(new Capacity(LIST.count() * 2), LIST);

    private VkCoreTypenameMap() {
    }

    @Override
    public @Mandatory Map<String, String> getMap() {
        return MAP;
    }
}
