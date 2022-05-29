package cz.mg.vulkantransformator.services.parser.splicer;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.utilities.code.Line;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

import java.util.Iterator;

public @Test class SplicerTest {
    public static void main(String[] args) {
        System.out.print("Running " + SplicerTest.class.getSimpleName() + " ... ");

        SplicerTest test = new SplicerTest();
        test.testSplice();

        System.out.println("OK");
    }

    private void testSplice() {
        testSplice(
            new List<>(
                createLine("typedef", "struct", "FooBar", "{"),
                createLine("int", "foo", ";"),
                createLine("}")
            ),
            new List<>(
                createLine("typedef", "struct", "FooBar", "{"),
                createLine("int", "foo", ";"),
                createLine("}")
            )
        );

        testSplice(
            new List<>(
                createLine("typedef", "struct", "FooBar", "{", "\\"),
                createLine("int", "foo", ";"),
                createLine("}")
            ),
            new List<>(
                createLine("typedef", "struct", "FooBar", "{", "int", "foo", ";"),
                createLine("}")
            )
        );

        testSplice(
            new List<>(
                createLine("typedef", "struct", "FooBar", "{"),
                createLine("int", "foo", ";", "\\"),
                createLine("}")
            ),
            new List<>(
                createLine("typedef", "struct", "FooBar", "{"),
                createLine("int", "foo", ";", "}")
            )
        );

        testSplice(
            new List<>(
                createLine("typedef", "struct", "FooBar", "{"),
                createLine("\\", "int", "foo", ";"),
                createLine("}")
            ),
            new List<>(
                createLine("typedef", "struct", "FooBar", "{"),
                createLine("\\", "int", "foo", ";"),
                createLine("}")
            )
        );

        testSplice(
            new List<>(
                createLine("typedef", "struct", "FooBar", "{"),
                createLine("int", "\\", "foo", ";"),
                createLine("}")
            ),
            new List<>(
                createLine("typedef", "struct", "FooBar", "{"),
                createLine("int", "\\", "foo", ";"),
                createLine("}")
            )
        );
    }

    private void testSplice(@Mandatory List<List<Token>> input, @Mandatory List<List<Token>> expectedOutput) {
        Splicer splicer = Splicer.getInstance();
        List<List<Token>> actualOutput = splicer.splice(input);

        Assert.assertEquals(expectedOutput.count(), actualOutput.count());

        Iterator<List<Token>> expectedOutputIterator = expectedOutput.iterator();
        Iterator<List<Token>> actualOutputIterator = actualOutput.iterator();
        while (expectedOutputIterator.hasNext() && actualOutputIterator.hasNext()) {
            List<Token> expectedLine = expectedOutputIterator.next();
            List<Token> actualLine = actualOutputIterator.next();

            Assert.assertEquals(expectedLine.count(), actualLine.count());

            Iterator<Token> expectedLineIterator = expectedLine.iterator();
            Iterator<Token> actualLineIterator = actualLine.iterator();
            while (expectedLineIterator.hasNext() && actualLineIterator.hasNext()) {
                Token expectedToken = expectedLineIterator.next();
                Token actualToken = actualLineIterator.next();

                Assert.assertEquals(expectedToken.getText(), actualToken.getText());
            }
        }
    }

    private @Mandatory List<Token> createLine(String... stringTokens) {
        List<Token> tokens = new List<>();

        for (String stringToken : stringTokens) {
            tokens.addLast(new Token(new Line(-1, stringToken), 0, stringToken.length(), TokenType.SPECIAL));
        }

        return tokens;
    }
}
