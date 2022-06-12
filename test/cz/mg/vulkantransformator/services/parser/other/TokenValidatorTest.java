package cz.mg.vulkantransformator.services.parser.other;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.utilities.code.Line;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

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

        Assert.assertExceptionNotThrown(() -> {
            validator.validate(createNameToken("FooBar"), "FooBar");
        });

        Assert.assertExceptionNotThrown(() -> {
            validator.validate(createSpecialToken("*"), "*");
        });

        Assert.assertExceptionThrown(ParseException.class, () -> {
            validator.validate(createNameToken("FooBar"), "Foo");
        });

        Assert.assertExceptionThrown(ParseException.class, () -> {
            validator.validate(createNameToken("Foo"), "FooBar");
        });

        Assert.assertExceptionThrown(ParseException.class, () -> {
            validator.validate(createNameToken("FooBar"), "*");
        });

        Assert.assertExceptionThrown(ParseException.class, () -> {
            validator.validate(createSpecialToken("*"), "FooBar");
        });
    }

    private void testValidateType() {
        TokenValidator validator = TokenValidator.getInstance();

        Assert.assertExceptionNotThrown(() -> {
            validator.validate(createNameToken("FooBar"), TokenType.NAME);
        });

        Assert.assertExceptionNotThrown(() -> {
            validator.validate(createSpecialToken("*"), TokenType.SPECIAL);
        });

        Assert.assertExceptionThrown(ParseException.class, () -> {
            validator.validate(createNameToken("FooBar"), TokenType.SPECIAL);
        });

        Assert.assertExceptionThrown(ParseException.class, () -> {
            validator.validate(createSpecialToken("*"), TokenType.NAME);
        });
    }

    private @Mandatory Token createNameToken(@Mandatory String text) {
        return new Token(new Line(-1, text), 0, text.length(), TokenType.NAME);
    }

    private @Mandatory Token createSpecialToken(@Mandatory String text) {
        return new Token(new Line(-1, text), 0, text.length(), TokenType.SPECIAL);
    }
}
