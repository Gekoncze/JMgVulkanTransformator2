package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.entities.preprocessor.Definition;
import cz.mg.vulkantransformator.utilities.code.Line;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

public @Test class DefinitionManagerTest {
    public static void main(String[] args) {
        System.out.print("Running " + DefinitionManagerTest.class.getSimpleName() + " ... ");

        DefinitionManagerTest test = new DefinitionManagerTest();
        test.test();

        System.out.println("OK");
    }

    private void test() {
        List<Definition> initialDefinitions = new List<>(
            createDefinition("First"),
            createDefinition("Second")
        );

        DefinitionManager definitionManager = new DefinitionManager(initialDefinitions);

        Assert.assertEquals(false, definitionManager.defined("Third"));
        Assert.assertEquals(true, definitionManager.defined("First"));
        Assert.assertEquals(true, definitionManager.defined("Second"));
        Assert.assertEquals(null, definitionManager.get("Third"));
        Assert.assertEquals(initialDefinitions.getFirst(), definitionManager.get("First"));
        Assert.assertEquals(initialDefinitions.getLast(), definitionManager.get("Second"));

        definitionManager.undefine("First");

        Assert.assertEquals(false, definitionManager.defined("First"));
        Assert.assertEquals(null, definitionManager.get("First"));

        Definition third = createDefinition("Third");
        definitionManager.define(third);

        Assert.assertEquals(true, definitionManager.defined("Third"));
        Assert.assertEquals(third, definitionManager.get("Third"));
    }

    private @Mandatory Definition createDefinition(@Mandatory String name) {
        return new Definition(createToken(name), new List<>(), new List<>());
    }

    private @Mandatory Token createToken(@Mandatory String text) {
        return new Token(new Line(-1, text), 0, text.length(), TokenType.SPECIAL);
    }
}
