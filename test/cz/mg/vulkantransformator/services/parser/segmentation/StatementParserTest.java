package cz.mg.vulkantransformator.services.parser.segmentation;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.utilities.code.Line;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.Token;

import java.util.Iterator;

public @Test class StatementParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + StatementParserTest.class.getSimpleName() + " ... ");

        StatementParserTest test = new StatementParserTest();
        test.testParse();

        System.out.println("OK");
    }

    private void testParse() {
        testParse(
            new List<>(new List<>())
        );

        testParse(
            new List<>(),
            ";"
        );

        testParse(
            new List<>(),
            ";", ";"
        );

        testParse(
            new List<List<String>>(
                new List<>("1")
            ),
            "1"
        );

        testParse(
            new List<List<String>>(
                new List<>("public", "void", "*", "foo")
            ),
            "public", "void", "*", "foo"
        );

        testParse(
            new List<List<String>>(
                new List<>("public", "void", "*", "foo")
            ),
            "public", "void", "*", "foo", ";"
        );

        testParse(
            new List<>(
                new List<>("public", "void", "*", "foo"),
                new List<>("1", "2")
            ),
            "public", "void", "*", "foo", ";",
            "1", "2"
        );

        testParse(
            new List<>(
                new List<>("public", "void", "*", "foo"),
                new List<>("{", "foo", ";", "bar", ";", "}", "1", "2")
            ),
            "public", "void", "*", "foo", ";",
            ";",
            "{", "foo", ";", "bar", ";", "}", "1", "2"
        );
    }

    private void testParse(@Mandatory List<List<String>> expectedStringStatements, String... stringTokens) {
        List<Token> tokens = new List<>();
        for (String stringToken : stringTokens) {
            tokens.addLast(new Token(new Line(-1, stringToken), 0, stringToken.length()));
        }

        StatementParser parser = StatementParser.getInstance();
        List<Statement> statements = parser.parse(tokens);

        Assert.assertEquals(expectedStringStatements.count(), statements.count());

        Iterator<List<String>> stringStatementIterator = expectedStringStatements.iterator();
        Iterator<Statement> statementIterator = statements.iterator();
        while (stringStatementIterator.hasNext() && statementIterator.hasNext()) {
            List<String> stringStatement = stringStatementIterator.next();
            Statement statement = statementIterator.next();

            Assert.assertEquals(stringStatement.count(), statement.getTokens().count());

            Iterator<String> stringTokenIterator = stringStatement.iterator();
            Iterator<Token> tokenIterator = statement.getTokens().iterator();
            while (stringTokenIterator.hasNext() && tokenIterator.hasNext()) {
                String stringToken = stringTokenIterator.next();
                Token token = tokenIterator.next();

                Assert.assertEquals(stringToken, token.getText());
            }
        }
    }
}
