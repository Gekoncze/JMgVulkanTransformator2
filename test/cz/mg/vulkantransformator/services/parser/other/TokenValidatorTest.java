package cz.mg.vulkantransformator.services.parser.other;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.entities.parser.code.Line;
import cz.mg.vulkantransformator.entities.parser.code.Token;
import cz.mg.vulkantransformator.entities.parser.code.TokenType;

public @Test class TokenValidatorTest {
    public static void main(String[] args) {
        System.out.print("Running " + TokenValidatorTest.class.getSimpleName() + " ... ");

        TokenValidatorTest test = new TokenValidatorTest();
        test.testValidateText();
        test.testValidateType();

        System.out.println("OK");
    }

    private void testValidateText() {
        TokenValidator validator = TokenValidator.getInstance();

        Assert.assertThatCode(() -> {
            validator.validate(createNameToken("FooBar"), "FooBar");
        }).doesNotThrowAnyException();

        Assert.assertThatCode(() -> {
            validator.validate(createSpecialToken("*"), "*");
        }).doesNotThrowAnyException();

        Assert.assertThatCode(() -> {
            validator.validate(createNameToken("FooBar"), "Foo");
        }).throwsException(ParseException.class);

        Assert.assertThatCode(() -> {
            validator.validate(createNameToken("Foo"), "FooBar");
        }).throwsException(ParseException.class);

        Assert.assertThatCode(() -> {
            validator.validate(createNameToken("FooBar"), "*");
        }).throwsException(ParseException.class);

        Assert.assertThatCode(() -> {
            validator.validate(createSpecialToken("*"), "FooBar");
        }).throwsException(ParseException.class);
    }

    private void testValidateType() {
        TokenValidator validator = TokenValidator.getInstance();

        Assert.assertThatCode(() -> {
            validator.validate(createNameToken("FooBar"), TokenType.NAME);
        }).doesNotThrowAnyException();

        Assert.assertThatCode(() -> {
            validator.validate(createSpecialToken("*"), TokenType.SPECIAL);
        }).doesNotThrowAnyException();

        Assert.assertThatCode(() -> {
            validator.validate(createNameToken("FooBar"), TokenType.SPECIAL);
        }).throwsException(ParseException.class);

        Assert.assertThatCode(() -> {
            validator.validate(createSpecialToken("*"), TokenType.NAME);
        }).throwsException(ParseException.class);
    }

    private @Mandatory Token createNameToken(@Mandatory String text) {
        return new Token(new Line(-1, text), 0, text.length(), TokenType.NAME, text);
    }

    private @Mandatory Token createSpecialToken(@Mandatory String text) {
        return new Token(new Line(-1, text), 0, text.length(), TokenType.SPECIAL, text);
    }
}
