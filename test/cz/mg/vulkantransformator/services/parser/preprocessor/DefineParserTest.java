package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.entities.preprocessor.Definition;
import cz.mg.vulkantransformator.services.parser.segmentation.TokenParser;
import cz.mg.vulkantransformator.utilities.code.Line;
import cz.mg.vulkantransformator.utilities.code.Token;

import java.util.Iterator;

public @Test class DefineParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + DefineParserTest.class.getSimpleName() + " ... ");

        DefineParserTest test = new DefineParserTest();
        test.testParse();

        System.out.println("OK");
    }

    private void testParse() {
        testParse(
            "#define FOO",
            "FOO",
            new List<>(),
            new List<>()
        );

        testParse(
            "#define FOO 1",
            "FOO",
            new List<>(),
            new List<>("1")
        );

        testParse(
            "#define FOOBAR foo bar",
            "FOOBAR",
            new List<>(),
            new List<>("foo", "bar")
        );

        testParse(
            "#define FOO(x)",
            "FOO",
            new List<>("x"),
            new List<>()
        );

        testParse(
            "#define FOO(x,y)",
            "FOO",
            new List<>("x", "y"),
            new List<>()
        );

        testParse(
            "#define FOOBAR(foo,bar) foo + bar",
            "FOOBAR",
            new List<>("foo", "bar"),
            new List<>("foo", "+", "bar")
        );
    }

    private void testParse(
        @Mandatory String line,
        @Mandatory String expectedName,
        @Mandatory List<String> expectedParameters,
        @Mandatory List<String> expectedExpressions
    ) {
        Definition definition = DefineParser.getInstance().parse(parseLine(line));
        Assert.assertEquals(expectedName, definition.getName().getText());

        Assert.assertEquals(expectedParameters.count(), definition.getParameters().count());
        Iterator<String> expectedParameterIterator = expectedParameters.iterator();
        Iterator<Token> actualParameterIterator = definition.getParameters().iterator();
        while (expectedParameterIterator.hasNext() && actualParameterIterator.hasNext()) {
            String expectedParameter = expectedParameterIterator.next();
            Token actualParameter = actualParameterIterator.next();
            Assert.assertEquals(expectedParameter, actualParameter.getText());
        }

        Assert.assertEquals(expectedExpressions.count(), definition.getExpression().count());
        Iterator<String> expectedExpressionIterator = expectedExpressions.iterator();
        Iterator<Token> actualExpressionsIterator = definition.getExpression().iterator();
        while (expectedExpressionIterator.hasNext() && actualExpressionsIterator.hasNext()) {
            String expectedExpression = expectedExpressionIterator.next();
            Token actualExpression = actualExpressionsIterator.next();
            Assert.assertEquals(expectedExpression, actualExpression.getText());
        }
    }


    private @Mandatory List<Token> parseLine(@Mandatory String line) {
        return TokenParser.getInstance().parse(
            new List<>(new Line(-1, line))
        ).getFirst();
    }
}
