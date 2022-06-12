package cz.mg.vulkantransformator.services.parser.other;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.utilities.code.Line;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

public @Test class TokenRemoverTest {
    public static void main(String[] args) {
        System.out.print("Running " + TokenRemoverTest.class.getSimpleName() + " ... ");

        TokenRemoverTest test = new TokenRemoverTest();
        test.testRemove();

        System.out.println("OK");
    }

    private void testRemove() {
        List<Token> tokens = new List<>(
            createNameToken("typedef"),
            createNameToken("struct"),
            createNameToken("Test"),
            createSpecialToken("{"),
            createNameToken("void"),
            createSpecialToken("*"),
            createNameToken("next"),
            createSpecialToken(";"),
            createSpecialToken("}"),
            createNameToken("Test"),
            createSpecialToken(";")
        );

        TokenRemover tokenRemover = TokenRemover.getInstance();

        // typedef struct Test { void * next ; } Test ;

        Assert.assertExceptionNotThrown(() -> {
            Assert.assertEquals("typedef", tokenRemover.removeFirst(tokens, "typedef").getText());
        });

        // struct Test { void * next ; } Test ;

        Assert.assertExceptionNotThrown(() -> {
            Assert.assertEquals("struct", tokenRemover.removeFirst(tokens, TokenType.NAME).getText());
        });

        // Test { void * next ; } Test ;

        Assert.assertExceptionThrown(ParseException.class, () -> {
            tokenRemover.removeFirst(tokens, "void");
        });

        // { void * next ; } Test ;

        Assert.assertExceptionThrown(ParseException.class, () -> {
            tokenRemover.removeFirst(tokens, TokenType.NAME);
        });

        // void * next ; } Test ;

        Assert.assertExceptionNotThrown(() -> {
            Assert.assertEquals(";", tokenRemover.removeLast(tokens, ";").getText());
        });

        // void * next ; } Test

        Assert.assertExceptionThrown(ParseException.class, () -> {
            tokenRemover.removeLast(tokens, TokenType.SPECIAL);
        });

        // void * next ; }

        Assert.assertExceptionNotThrown(() -> {
            Assert.assertEquals("}", tokenRemover.removeLast(tokens, TokenType.SPECIAL).getText());
        });

        // void * next ;

        Assert.assertExceptionThrown(ParseException.class, () -> {
            tokenRemover.removeLast(tokens, "next");
        });

        // void * next

        Assert.assertEquals(3, tokens.count());
        Assert.assertEquals("void", tokens.get(0).getText());
        Assert.assertEquals("*", tokens.get(1).getText());
        Assert.assertEquals("next", tokens.get(2).getText());

        Assert.assertExceptionThrown(ParseException.class, () -> {
            tokenRemover.verifyNoMoreTokens(tokens);
        });

        tokens.clear();

        Assert.assertExceptionNotThrown(() -> {
            tokenRemover.verifyNoMoreTokens(tokens);
        });
    }

    private @Mandatory Token createNameToken(@Mandatory String text) {
        return new Token(new Line(-1, text), 0, text.length(), TokenType.NAME);
    }

    private @Mandatory Token createSpecialToken(@Mandatory String text) {
        return new Token(new Line(-1, text), 0, text.length(), TokenType.SPECIAL);
    }
}
