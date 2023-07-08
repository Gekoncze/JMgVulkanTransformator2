package cz.mg.vulkantransformator.services.parser.other;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.entities.parser.code.Line;
import cz.mg.vulkantransformator.entities.parser.code.Token;
import cz.mg.vulkantransformator.entities.parser.code.TokenType;

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

        Assert.assertThatCode(() -> {
            Assert.assertEquals("typedef", tokenRemover.removeFirst(tokens, "typedef").getText());
        }).doesNotThrowAnyException();

        // struct Test { void * next ; } Test ;

        Assert.assertThatCode(() -> {
            Assert.assertEquals("struct", tokenRemover.removeFirst(tokens, TokenType.NAME).getText());
        }).doesNotThrowAnyException();

        // Test { void * next ; } Test ;

        Assert.assertThatCode(() -> {
            tokenRemover.removeFirst(tokens, "void");
        }).throwsException(ParseException.class);

        // { void * next ; } Test ;

        Assert.assertThatCode(() -> {
            tokenRemover.removeFirst(tokens, TokenType.NAME);
        }).throwsException(ParseException.class);

        // void * next ; } Test ;

        Assert.assertThatCode(() -> {
            Assert.assertEquals(";", tokenRemover.removeLast(tokens, ";").getText());
        }).doesNotThrowAnyException();

        // void * next ; } Test

        Assert.assertThatCode(() -> {
            tokenRemover.removeLast(tokens, TokenType.SPECIAL);
        }).throwsException(ParseException.class);

        // void * next ; }

        Assert.assertThatCode(() -> {
            Assert.assertEquals("}", tokenRemover.removeLast(tokens, TokenType.SPECIAL).getText());
        }).doesNotThrowAnyException();

        // void * next ;

        Assert.assertThatCode(() -> {
            tokenRemover.removeLast(tokens, "next");
        }).throwsException(ParseException.class);

        // void * next

        Assert.assertEquals(3, tokens.count());
        Assert.assertEquals("void", tokens.get(0).getText());
        Assert.assertEquals("*", tokens.get(1).getText());
        Assert.assertEquals("next", tokens.get(2).getText());

        Assert.assertThatCode(() -> {
            tokenRemover.verifyNoMoreTokens(tokens);
        }).throwsException(ParseException.class);

        tokens.clear();

        Assert.assertThatCode(() -> {
            tokenRemover.verifyNoMoreTokens(tokens);
        }).doesNotThrowAnyException();

        Assert.assertThatCode(() -> {
            tokenRemover.removeFirst(tokens, "x");
        }).throwsException(ParseException.class);

        Assert.assertThatCode(() -> {
            tokenRemover.removeFirst(tokens, TokenType.NAME);
        }).throwsException(ParseException.class);

        Assert.assertThatCode(() -> {
            tokenRemover.removeLast(tokens, "x");
        }).throwsException(ParseException.class);

        Assert.assertThatCode(() -> {
            tokenRemover.removeLast(tokens, TokenType.NAME);
        }).throwsException(ParseException.class);
    }

    private @Mandatory Token createNameToken(@Mandatory String text) {
        return new Token(new Line(-1, text), 0, text.length(), TokenType.NAME, text);
    }

    private @Mandatory Token createSpecialToken(@Mandatory String text) {
        return new Token(new Line(-1, text), 0, text.length(), TokenType.SPECIAL, text);
    }
}
