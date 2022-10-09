package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.entities.parser.preprocessor.Definition;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.services.parser.segmentation.TokenParser;
import cz.mg.vulkantransformator.entities.parser.code.Line;
import cz.mg.vulkantransformator.entities.parser.code.Token;
import cz.mg.vulkantransformator.entities.parser.code.TokenType;

import java.util.Iterator;

public @Test class PreprocessorTest {
    public static void main(String[] args) {
        System.out.print("Running " + PreprocessorTest.class.getSimpleName() + " ... ");

        PreprocessorTest test = new PreprocessorTest();
        test.testDefine();
        test.testIfdef();
        test.testError();

        System.out.println("OK");
    }

    private void testDefine() {
        TokenParser tokenParser = TokenParser.getInstance();
        Preprocessor preprocessor = Preprocessor.getInstance();

        List<Definition> definitions = new List<>();

        definitions.addLast(
            new Definition(
                createToken("VALUE"),
                new List<>(),
                new List<>(createToken("5"))
            )
        );

        List<Token> actualTokens = preprocessor.preprocess(
            tokenParser.parse(new List<>(
                new Line(0, "#define TEST foo"),
                new Line(1, "int TEST = VALUE;"),
                new Line(2, "#undef TEST"),
                new Line(3, "int TEST = VALUE;"),
                new Line(4, "#define X")
            )),
            definitions
        );

        Assert.assertEquals(2, definitions.count());
        Assert.assertEquals("VALUE", definitions.get(0).getName().getText());
        Assert.assertEquals("X", definitions.get(1).getName().getText());

        testRemainingTokens(new List<>("int", "foo", "=", "5", ";", "int", "TEST", "=", "5", ";"), actualTokens);
    }

    private void testIfdef() {
        TokenParser tokenParser = TokenParser.getInstance();
        Preprocessor preprocessor = Preprocessor.getInstance();

        List<Definition> definitions = new List<>();

        definitions.addLast(
            new Definition(
                createToken("X"),
                new List<>(),
                new List<>()
            )
        );

        List<Token> actualTokens = preprocessor.preprocess(
            tokenParser.parse(new List<>(
                new Line(0, "#ifndef X"),
                new Line(1, "int foo = 0;"),
                new Line(2, "#endif"),
                new Line(3, "#ifdef X"),
                new Line(4, "int foo = 1;"),
                new Line(5, "#endif")
            )),
            definitions
        );

        testRemainingTokens(new List<>("int", "foo", "=", "1", ";"), actualTokens);
    }

    private void testError() {
        TokenParser tokenParser = TokenParser.getInstance();
        Preprocessor preprocessor = Preprocessor.getInstance();

        Assert.assertExceptionThrown(ParseException.class, () -> {
            preprocessor.preprocess(
                tokenParser.parse(new List<>(
                    new Line(0, "#ifndef X"),
                    new Line(1, "#error nope"),
                    new Line(2, "#endif")
                )),
                new List<>(createDefinition("foo"))
            );
        });

        Assert.assertExceptionNotThrown(() -> {
            preprocessor.preprocess(
                tokenParser.parse(new List<>(
                    new Line(0, "#ifndef X"),
                    new Line(1, "#error nope"),
                    new Line(2, "#endif")
                )),
                new List<>(createDefinition("X"))
            );
        });

        Assert.assertExceptionNotThrown(() -> {
            preprocessor.preprocess(
                tokenParser.parse(new List<>(
                    new Line(0, "#ifdef X"),
                    new Line(1, "#error nope"),
                    new Line(2, "#endif")
                )),
                new List<>(createDefinition("foo"))
            );
        });

        Assert.assertExceptionThrown(ParseException.class, () -> {
            preprocessor.preprocess(
                tokenParser.parse(new List<>(
                    new Line(0, "#ifdef X"),
                    new Line(1, "#error nope"),
                    new Line(2, "#endif")
                )),
                new List<>(createDefinition("X"))
            );
        });
    }

    private void testRemainingTokens(@Mandatory List<String> expectedTokens, @Mandatory List<Token> actualTokens) {
        Assert.assertEquals(expectedTokens.count(), actualTokens.count());

        Iterator<String> expectedTokenIterator = expectedTokens.iterator();
        Iterator<Token> actualTokenIterator = actualTokens.iterator();
        while (expectedTokenIterator.hasNext() && actualTokenIterator.hasNext()) {
            String expectedToken = expectedTokenIterator.next();
            Token actualToken = actualTokenIterator.next();

            Assert.assertEquals(expectedToken, actualToken.getText());
        }
    }

    private @Mandatory Token createToken(@Mandatory String text) {
        return new Token(new Line(-1, text), 0, text.length(), TokenType.SPECIAL, text);
    }

    private @Mandatory Definition createDefinition(@Mandatory String name) {
        return new Definition(createToken(name), new List<>(), new List<>());
    }
}
