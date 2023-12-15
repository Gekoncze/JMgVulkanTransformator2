package cz.mg.vulkantransformator;

import cz.mg.annotations.classes.Test;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcherTest;
import cz.mg.vulkantransformator.services.parser.other.TokenRemoverTest;
import cz.mg.vulkantransformator.services.parser.other.TokenValidatorTest;
import cz.mg.vulkantransformator.services.parser.segmentation.StatementParserTest;

public @Test class AllTests {
    public static void main(String[] args) {
        // cz.mg.vulkantransformator.services.matcher
        PatternMatcherTest.main(args);

        // cz.mg.vulkantransformator.services.other
        TokenRemoverTest.main(args);
        TokenValidatorTest.main(args);

        // cz.mg.vulkantransformator.services.segmentation
        StatementParserTest.main(args);
    }
}
