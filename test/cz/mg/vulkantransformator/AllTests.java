package cz.mg.vulkantransformator;

import cz.mg.annotations.classes.Test;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcherTest;
import cz.mg.vulkantransformator.services.parser.preprocessor.DefinitionManagerTest;
import cz.mg.vulkantransformator.services.parser.preprocessor.ErrorParserTest;
import cz.mg.vulkantransformator.services.parser.preprocessor.PreprocessorTest;
import cz.mg.vulkantransformator.services.parser.segmentation.LineParserTest;
import cz.mg.vulkantransformator.services.parser.segmentation.StatementParserTest;
import cz.mg.vulkantransformator.services.parser.segmentation.TokenParserTest;
import cz.mg.vulkantransformator.services.parser.splicer.SplicerTest;

public @Test class AllTests {
    public static void main(String[] args) {
        LineParserTest.main(args);
        TokenParserTest.main(args);
        StatementParserTest.main(args);
        PatternMatcherTest.main(args);
        PreprocessorTest.main(args);
        DefinitionManagerTest.main(args);
        ErrorParserTest.main(args);
        SplicerTest.main(args);
    }
}
