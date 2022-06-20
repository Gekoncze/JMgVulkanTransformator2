package cz.mg.vulkantransformator.services.parser.segmentation;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.Line;

import java.util.Iterator;

public @Test class TokenParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + TokenParserTest.class.getSimpleName() + " ... ");

        TokenParserTest test = new TokenParserTest();
        test.testParseSingleLine();
        test.testParseMultiLine();

        System.out.println("OK");
    }

    private void testParseSingleLine() {
        testParseSingleLine("abc", "abc");
        testParseSingleLine("1.0", "1.0");
        testParseSingleLine("1.0f", "1.0f");
        testParseSingleLine("1.0F", "1.0F");
        testParseSingleLine("02", "02");
        testParseSingleLine("0x2", "0x2");
        testParseSingleLine("1", "1");
        testParseSingleLine(";!*", ";", "!", "*");
        testParseSingleLine(" ");
        testParseSingleLine("// yay");
        testParseSingleLine("''", "''");
        testParseSingleLine("\"\"", "\"\"");
        testParseSingleLine("[]", "[", "]");
        testParseSingleLine("a/b", "a", "/", "b");
        testParseSingleLine("a//", "a");
        testParseSingleLine("a_b", "a_b");
        testParseSingleLine("_b", "_b");

        testParseSingleLine(
            "    const* float * test[5] = {-1.0f}; // yay!",
            "const", "*", "float", "*", "test", "[", "5", "]", "=", "{", "-", "1.0f", "}", ";"
        );

        testParseSingleLine(
            "const* char* text = \"abc foo // \\\" 1.23 bar\";",
            "const", "*", "char", "*", "text", "=", "\"abc foo // \\\" 1.23 bar\"", ";"
        );

        testParseSingleLine(
            "char ch = 'a\\'';",
            "char", "ch", "=", "'a\\''", ";"
        );
    }

    private void testParseSingleLine(@Mandatory String line, @Mandatory String... expectedTokens) {
        TokenParser tokenParser = TokenParser.getInstance();
        List<List<Token>> linesTokens = tokenParser.parse(new List<>(new Line(-1, line)));
        if (expectedTokens.length == 0 && linesTokens.isEmpty()) {
            return;
        }
        List<Token> tokens = linesTokens.getFirst();
        List<String> actualTokens = new List<>();
        for (Token token : tokens) {
            actualTokens.addLast(token.getText());
        }
        validate(new List<>(expectedTokens), actualTokens);
    }

    private void validate(@Mandatory List<String> expectedTokens, @Mandatory List<String> actualTokens) {
        try {
            Assert.assertEquals(expectedTokens.count(), actualTokens.count());
            Iterator<String> expectedTokenIterator = expectedTokens.iterator();
            Iterator<String> actualTokenIterator = actualTokens.iterator();
            while (expectedTokenIterator.hasNext() && actualTokenIterator.hasNext()) {
                String expectedToken = expectedTokenIterator.next();
                String actualToken = actualTokenIterator.next();
                Assert.assertEquals(expectedToken, actualToken);
            }
        } catch (RuntimeException e) {
            String expectedMessage = String.join("\n", expectedTokens);
            String actualMessage = String.join("\n", actualTokens);
            String additionalMessage = "\nExpectation:\n" + expectedMessage + "\n\nReality:\n" + actualMessage;
            throw new RuntimeException(e.getMessage() + "\n" + additionalMessage, e);
        }
    }

    private void testParseMultiLine() {
        testParseMultiLine(
            new List<>(),
            new List<>()
        );

        testParseMultiLine(
            new List<>("abc"),
            new List<List<String>>(new List<>("abc"))
        );

        testParseMultiLine(
            new List<>("abc", "foo bar"),
            new List<>(new List<>("abc"), new List<>("foo", "bar"))
        );

        testParseMultiLine(
            new List<>("//"),
            new List<>()
        );

        testParseMultiLine(
            new List<>("// abc"),
            new List<>()
        );

        testParseMultiLine(
            new List<>("foo // abc"),
            new List<List<String>>(new List<>("foo"))
        );

        testParseMultiLine(
            new List<>("/**/"),
            new List<>()
        );

        testParseMultiLine(
            new List<>("/* abc */"),
            new List<>()
        );

        testParseMultiLine(
            new List<>("foo/*abc*/bar"),
            new List<List<String>>(new List<>("foo", "bar"))
        );

        testParseMultiLine(
            new List<>(
                "foo/*",
                "*/bar"
            ),
            new List<>(
                new List<>("foo"),
                new List<>("bar")
            )
        );

        testParseMultiLine(
            new List<>(
                "foo/* 111",
                "222 */bar"
            ),
            new List<>(
                new List<>("foo"),
                new List<>("bar")
            )
        );

        testParseMultiLine(
            new List<>("/*", "*/"),
            new List<>()
        );

        testParseMultiLine(
            new List<>("/*", " ", "*/"),
            new List<>()
        );

        testParseMultiLine(
            new List<>(
                "foo/*",
                "000",
                "*/bar"
            ),
            new List<>(
                new List<>("foo"),
                new List<>("bar")
            )
        );

        testParseMultiLine(
            new List<>(
                "typedef struct FooBar {/*",
                "    int i;",
                "*/} FooBar;"
            ),
            new List<>(
                new List<>("typedef", "struct", "FooBar", "{"),
                new List<>("}", "FooBar", ";")
            )
        );
    }

    private void testParseMultiLine(@Mandatory List<String> stringLines, @Mandatory List<List<String>> expectedTokens) {
        List<Line> lines = new List<>();
        for (String stringLine : stringLines) {
            lines.addLast(new Line(-1, stringLine));
        }

        TokenParser parser = TokenParser.getInstance();
        List<List<Token>> actualTokens = parser.parse(lines);

        Assert.assertEquals(expectedTokens.count(), actualTokens.count());

        Iterator<List<String>> expectedTokensIterator = expectedTokens.iterator();
        Iterator<List<Token>> actualTokensIterator = actualTokens.iterator();
        while (expectedTokensIterator.hasNext() && actualTokensIterator.hasNext()) {
            List<String> expectedLineTokens = expectedTokensIterator.next();
            List<Token> actualLineTokens = actualTokensIterator.next();

            Assert.assertEquals(expectedLineTokens.count(), actualLineTokens.count());

            Iterator<String> expectedLineTokenIterator = expectedLineTokens.iterator();
            Iterator<Token> actualLineTokenIterator = actualLineTokens.iterator();
            while (expectedLineTokenIterator.hasNext() && actualLineTokenIterator.hasNext()) {
                String expectedToken = expectedLineTokenIterator.next();
                Token actualToken = actualLineTokenIterator.next();

                Assert.assertEquals(expectedToken, actualToken.getText());
            }
        }
    }
}
