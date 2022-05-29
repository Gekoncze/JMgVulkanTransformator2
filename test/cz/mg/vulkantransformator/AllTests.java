package cz.mg.vulkantransformator;

import cz.mg.annotations.classes.Test;
import cz.mg.vulkantransformator.services.parser.LineParserTest;
import cz.mg.vulkantransformator.services.parser.PatternMatcherTest;
import cz.mg.vulkantransformator.services.parser.StatementParserTest;
import cz.mg.vulkantransformator.services.parser.TokenParserTest;
import cz.mg.vulkantransformator.services.preprocessor.PreprocessorTest;

public @Test class AllTests {
    public static void main(String[] args) {
        LineParserTest.main(args);
        TokenParserTest.main(args);
        StatementParserTest.main(args);
        PatternMatcherTest.main(args);
        PreprocessorTest.main(args);
    }
}
