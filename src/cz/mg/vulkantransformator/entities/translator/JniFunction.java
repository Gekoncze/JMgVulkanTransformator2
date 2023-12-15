package cz.mg.vulkantransformator.entities.translator;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.collections.list.List;

/**
 * @deprecated Should be replaced by a factory creating CFunction.
 */
@Deprecated
public @Entity class JniFunction {
    private Boolean sstatic;
    private String output;
    private String className;
    private String name;
    private List<String> input = new List<>();
    private List<String> lines = new List<>();

    public JniFunction() {
    }

    @Required
    public Boolean isStatic() {
        return sstatic;
    }

    public void setStatic(Boolean value) {
        this.sstatic = value;
    }

    @Required
    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    @Required
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Required
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Required
    public List<String> getInput() {
        return input;
    }

    public void setInput(List<String> input) {
        this.input = input;
    }

    @Required
    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }
}
