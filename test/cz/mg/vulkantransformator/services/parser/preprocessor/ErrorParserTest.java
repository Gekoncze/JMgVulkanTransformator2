package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.services.parser.segmentation.TokenParser;
import cz.mg.vulkantransformator.entities.parser.code.Line;
import cz.mg.vulkantransformator.entities.parser.code.Token;

public @Test class ErrorParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + ErrorParserTest.class.getSimpleName() + " ... ");

        ErrorParserTest test = new ErrorParserTest();
        test.testParse();

        System.out.println("OK");
    }

    private void testParse() {
        testParse("#error test", "test");
        testParse("#error \"test\"", "\"test\"");
        testParse("#error ! foo bar !", "! foo bar !");
        testParse("#error \"! foo bar !\"", "\"! foo bar !\"");
    }

    private void testParse(@Mandatory String line, @Mandatory String errorMessage) {
        ParseException exception = Assert.assertExceptionThrown(ParseException.class, () -> {
            ErrorParser.getInstance().parse(parseLine(line));
        });

        Assert.assertEquals(true, exception.getMessage().contains(errorMessage));
    }

    private @Mandatory List<Token> parseLine(@Mandatory String line) {
        return TokenParser.getInstance().parse(
            new List<>(new Line(-1, line))
        ).getFirst();
    }
}
