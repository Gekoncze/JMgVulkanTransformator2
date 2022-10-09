package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Utility;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.list.ListItem;
import cz.mg.collections.map.Map;
import cz.mg.vulkantransformator.entities.parser.preprocessor.Definition;

public @Utility class DefinitionManager {
    private final @Mandatory List<Definition> list;
    private final @Mandatory Map<String, Definition> map;

    public DefinitionManager(@Mandatory List<Definition> list) {
        this.list = list;
        this.map = new Map<>(100);
        for (Definition definition : list) {
            map.set(definition.getName().getText(), definition);
        }
    }

    public void define(@Mandatory Definition definition) {
        list.addLast(definition);
        map.set(definition.getName().getText(), definition);
    }

    public @Optional Definition get(@Mandatory String name) {
        return map.getOptional(name);
    }

    public boolean defined(@Mandatory String name) {
        return map.getOptional(name) != null;
    }

    public void undefine(@Mandatory String name) {
        for (
            ListItem<Definition> item = list.getFirstItem();
            item != null;
            item = item.getNextItem()
        ) {
            if (item.get().getName().getText().equals(name)) {
                list.remove(item);
            }
        }

        map.remove(name);
    }
}
