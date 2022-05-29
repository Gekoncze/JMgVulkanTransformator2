package cz.mg.vulkantransformator.services.parser.matcher;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.utilities.code.Line;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.Token;

public @Test class PatternMatcherTest {
    public static void main(String[] args) {
        System.out.print("Running " + PatternMatcherTest.class.getSimpleName() + " ... ");

        PatternMatcherTest test = new PatternMatcherTest();
        test.testMatch();

        System.out.println("OK");
    }

    private void testMatch() {
        PatternMatcher patternMatcher = PatternMatcher.getInstance();

        Assert.assertExceptionNotThrown(() -> {
            patternMatcher.matches(
                createStatement("typedef", "struct", "test", "{", "}", ";"),
                Matchers.text("typedef"),
                Matchers.text("struct"),
                Matchers.any(),
                Matchers.text("{")
            );
        });

        Assert.assertEquals(true, patternMatcher.matches(
            createStatement("typedef", "struct", "test", "{", "}", ";"),
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
        ));

        Assert.assertEquals(true, patternMatcher.matches(
            createStatement("typedef", "struct", "foo", "{", "}", ";"),
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
        ));

        Assert.assertExceptionNotThrown(() -> {
            patternMatcher.matches(
                createStatement("typedef", "struct"),
                Matchers.text("typedef"),
                Matchers.text("struct"),
                Matchers.any(),
                Matchers.text("{")
            );
        });

        Assert.assertEquals(false, patternMatcher.matches(
            createStatement("typedef", "struct"),
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
        ));

        Assert.assertEquals(false, patternMatcher.matches(
            createStatement("typedef", "struct"),
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
        ));

        Assert.assertEquals(false, patternMatcher.matches(
            createStatement("pipedef", "struct", "test", "{", "}", ";"),
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
        ));

        Assert.assertEquals(false, patternMatcher.matches(
            createStatement("typedef", "struct", "{", "}", ";"),
            Matchers.text("typedef"),
            Matchers.text("struct"),
            Matchers.any(),
            Matchers.text("{")
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
        return new Token(new Line(-1, text), 0, text.length());
    }
}
