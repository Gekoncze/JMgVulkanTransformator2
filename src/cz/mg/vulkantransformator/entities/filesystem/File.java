package cz.mg.vulkantransformator.entities.filesystem;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;

public @Entity class File {
    private String name;
    private List<String> lines;

    public File() {
    }

    public File(String name, List<String> lines) {
        this.name = name;
        this.lines = lines;
    }

    @Value
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Part
    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }
}
