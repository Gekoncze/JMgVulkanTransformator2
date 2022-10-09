package cz.mg.vulkantransformator.services.parser.segmentation;

import cz.mg.annotations.classes.Test;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.vulkantransformator.entities.parser.code.Line;

public @Test class LineParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + LineParserTest.class.getSimpleName() + " ... ");

        LineParserTest test = new LineParserTest();
        test.testParse();

        System.out.println("OK");
    }

    private void testParse() {
        testParse(new String[]{});
        testParse("");
        testParse("abc");
        testParse(
            "abc foo bar",
            "",
            "; aaa"
        );
        testParse(
            "abc foo bar",
            ""
        );
        testParse(
            "",
            "abc foo bar"
        );
    }

    private void testParse(String... stringLines) {
        LineParser parser = LineParser.getInstance();
        List<Line> lines = parser.parse(new List<>(stringLines));

        Assert.assertEquals(stringLines.length, lines.count());

        int i = 0;
        for (Line line : lines) {
            Assert.assertEquals(i, line.getId());
            Assert.assertEquals(stringLines[i], line.getText());
            i++;
        }
    }
}
