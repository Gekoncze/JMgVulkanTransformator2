package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.test.Assert;

public class NumberParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + NumberParserTest.class.getSimpleName() + " ... ");

        NumberParserTest test = new NumberParserTest();
        test.testParseEmpty();
        test.testParseSimple();
        test.testParseExtended();

        System.out.println("OK");
    }

    private final @Service NumberParser parser = NumberParser.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse("");
        }).throwsException(NumberFormatException.class);
    }

    private void testParseSimple() {
        Assert.assertEquals(1, parser.parse("1"));
        Assert.assertEquals(77, parser.parse("77"));
        Assert.assertEquals(123, parser.parse("123"));
    }

    private void testParseExtended() {
        Assert.assertEquals(1, parser.parse("1U"));
        Assert.assertEquals(1, parser.parse("1L"));
        Assert.assertEquals(1, parser.parse("1UL"));
        Assert.assertEquals(69, parser.parse("69U"));
        Assert.assertEquals(69, parser.parse("69L"));
        Assert.assertEquals(69, parser.parse("69UL"));
    }
}
