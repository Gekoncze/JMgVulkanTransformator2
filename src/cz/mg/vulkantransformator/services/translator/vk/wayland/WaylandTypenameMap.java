package cz.mg.vulkantransformator.services.translator.vk.wayland;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.components.Capacity;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.vulkantransformator.services.translator.TypenameMap;

public @Service class WaylandTypenameMap implements TypenameMap {
    private static @Optional WaylandTypenameMap instance;

    public static @Mandatory WaylandTypenameMap getInstance() {
        if (instance == null) {
            instance = new WaylandTypenameMap();
        }
        return instance;
    }

    private static final List<Pair<String, String>> LIST = new List<>(
        new Pair<>("wl_display", "WlDisplay"),
        new Pair<>("wl_surface", "WlSurface")
    );

    private static final Map<String, String> MAP = new Map<>(new Capacity(LIST.count() * 2), LIST);

    private WaylandTypenameMap() {
    }

    @Override
    public @Mandatory Map<String, String> getMap() {
        return MAP;
    }
}
