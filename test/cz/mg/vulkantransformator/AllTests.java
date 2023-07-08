package cz.mg.vulkantransformator;

import cz.mg.annotations.classes.Test;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcherTest;
import cz.mg.vulkantransformator.services.parser.other.TokenRemoverTest;
import cz.mg.vulkantransformator.services.parser.other.TokenValidatorTest;
import cz.mg.vulkantransformator.services.parser.preprocessor.*;
import cz.mg.vulkantransformator.services.parser.segmentation.LineParserTest;
import cz.mg.vulkantransformator.services.parser.segmentation.StatementParserTest;
import cz.mg.vulkantransformator.services.parser.segmentation.TokenParserTest;
import cz.mg.vulkantransformator.services.parser.splicer.SplicerTest;

public @Test class AllTests {
    public static void main(String[] args) {
        // cz.mg.vulkantransformator.services.matcher
        PatternMatcherTest.main(args);

        // cz.mg.vulkantransformator.services.other
        TokenRemoverTest.main(args);
        TokenValidatorTest.main(args);

        // cz.mg.vulkantransformator.services.preprocessor
        DefineParserTest.main(args);
        DefinitionManagerTest.main(args);
        ErrorParserTest.main(args);
        PreprocessorTest.main(args);
        TokenPreprocessorTest.main(args);

        // cz.mg.vulkantransformator.services.segmentation
        LineParserTest.main(args);
        StatementParserTest.main(args);
        TokenParserTest.main(args);

        // cz.mg.vulkantransformator.services.splicer
        SplicerTest.main(args);
    }
}
