package cz.mg.vulkantransformator.services.translator.vk.xcb;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.components.Capacity;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.vulkantransformator.services.translator.TypenameMap;

public @Service class XcbTypenameMap implements TypenameMap {
    private static @Optional XcbTypenameMap instance;

    public static @Mandatory XcbTypenameMap getInstance() {
        if (instance == null) {
            instance = new XcbTypenameMap();
        }
        return instance;
    }

    private static final List<Pair<String, String>> LIST = new List<>(
        new Pair<>("xcb_connection_t", "XcbConnection"),
        new Pair<>("xcb_visualid_t", "XcbVisualId"),
        new Pair<>("xcb_window_t", "XcbWindow")
    );

    private static final Map<String, String> MAP = new Map<>(new Capacity(LIST.count() * 2), LIST);

    private XcbTypenameMap() {
    }

    @Override
    public @Mandatory Map<String, String> getMap() {
        return MAP;
    }
}
