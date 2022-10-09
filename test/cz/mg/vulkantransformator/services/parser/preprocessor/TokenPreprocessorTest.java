package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.entities.parser.preprocessor.Definition;
import cz.mg.vulkantransformator.services.parser.segmentation.TokenParser;
import cz.mg.vulkantransformator.entities.parser.code.Line;
import cz.mg.vulkantransformator.entities.parser.code.Token;

import java.util.Iterator;

public @Test class TokenPreprocessorTest {
    public static void main(String[] args) {
        System.out.print("Running " + TokenPreprocessorTest.class.getSimpleName() + " ... ");

        TokenPreprocessorTest test = new TokenPreprocessorTest();
        test.testPreprocess();

        System.out.println("OK");
    }

    private void testPreprocess() {
        List<Definition> definitions = new List<>(
            parseDefinition("#define EMPTY"),
            parseDefinition("#define ZERO foo"),
            parseDefinition("#define SINGLE(x) one x##bar two")
        );

        testPreprocess(
            "foo",
            definitions,
            new List<>("foo")
        );

        testPreprocess(
            "foo bar",
            definitions,
            new List<>("foo", "bar")
        );

        testPreprocess(
            "EMPTY bar",
            definitions,
            new List<>("bar")
        );

        testPreprocess(
            "ZERO bar",
            definitions,
            new List<>("foo", "bar")
        );

        testPreprocess(
            "SINGLE(foo) three",
            definitions,
            new List<>("one", "foobar", "two", "three")
        );
    }

    private void testPreprocess(
        @Mandatory String line,
        @Mandatory List<Definition> definitions,
        @Mandatory List<String> expectedTokens
    ) {
        TokenPreprocessor preprocessor = TokenPreprocessor.getInstance();
        List<Token> actualTokens = preprocessor.preprocess(
            parseLine(line),
            new DefinitionManager(definitions)
        );
        Assert.assertEquals(expectedTokens.count(), actualTokens.count());

        Iterator<String> expectedTokenIterator = expectedTokens.iterator();
        Iterator<Token> actualTokenIterator = actualTokens.iterator();
        while (expectedTokenIterator.hasNext() && actualTokenIterator.hasNext()) {
            String expectedToken = expectedTokenIterator.next();
            Token actualToken = actualTokenIterator.next();
            Assert.assertEquals(expectedToken, actualToken.getText());
        }
    }

    private @Mandatory List<Token> parseLine(@Mandatory String line) {
        return TokenParser.getInstance().parse(new List<>(new Line(-1, line))).getFirst();
    }

    private @Mandatory Definition parseDefinition(@Mandatory String line) {
        return DefineParser.getInstance().parse(parseLine(line));
    }
}
