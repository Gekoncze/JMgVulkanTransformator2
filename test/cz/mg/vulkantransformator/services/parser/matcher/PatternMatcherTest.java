package cz.mg.vulkantransformator.services.parser.matcher;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.entities.parser.code.Line;
import cz.mg.vulkantransformator.entities.parser.code.Statement;
import cz.mg.vulkantransformator.entities.parser.code.Token;
import cz.mg.vulkantransformator.entities.parser.code.TokenType;

public @Test class PatternMatcherTest {
    public static void main(String[] args) {
        System.out.print("Running " + PatternMatcherTest.class.getSimpleName() + " ... ");

        PatternMatcherTest test = new PatternMatcherTest();
        test.testMatch();

        System.out.println("OK");
    }

    private void testMatch() {
        PatternMatcher patternMatcher = PatternMatcher.getInstance();

        Assert.assertThatCode(() -> {
            patternMatcher.matches(
                createStatement("typedef", "struct", "test", "{", "}", ";"),
                false,
                Matchers.text("typedef"),
                Matchers.text("struct"),
                Matchers.any(),
                Matchers.text("{")
            );
        }).doesNotThrowAnyException();

        Assert.assertEquals(true, patternMatcher.matches(
            createStatement("typedef", "struct", "test", "{", "}", ";"),
            false,
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
        ));

        Assert.assertEquals(true, patternMatcher.matches(
            createStatement("typedef", "struct", "foo", "{", "}", ";"),
            false,
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
        ));

        Assert.assertThatCode(() -> {
            patternMatcher.matches(
                createStatement("typedef", "struct"),
                false,
                Matchers.text("typedef"),
                Matchers.text("struct"),
                Matchers.any(),
                Matchers.text("{")
            );
        }).doesNotThrowAnyException();

        Assert.assertEquals(false, patternMatcher.matches(
            createStatement("typedef", "struct"),
            false,
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
        ));

        Assert.assertEquals(false, patternMatcher.matches(
            createStatement("typedef", "struct"),
            false,
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
        ));

        Assert.assertEquals(false, patternMatcher.matches(
            createStatement("pipedef", "struct", "test", "{", "}", ";"),
            false,
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
        ));

        Assert.assertEquals(false, patternMatcher.matches(
            createStatement("typedef", "struct", "{", "}", ";"),
            false,
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
        ));

        Assert.assertEquals(true, patternMatcher.matches(
            createStatement("typedef", "uint32_t", "test"),
            true,
            Matchers.text("typedef"),
            Matchers.text("uint32_t"),
            Matchers.any()
        ));

        Assert.assertEquals(false, patternMatcher.matches(
            createStatement("typedef", "uint32_t", "*", "test"),
            true,
            Matchers.text("typedef"),
            Matchers.text("uint32_t"),
            Matchers.any()
        ));
    }

    private @Mandatory Statement createStatement(String... tokens) {
        Statement statement = new Statement();
        for (String token : tokens) {
            statement.getTokens().addLast(createToken(token));
        }
        return statement;
    }

    private @Mandatory Token createToken(@Mandatory String text) {
        return new Token(new Line(-1, text), 0, text.length(), TokenType.SPECIAL, text);
    }
}
