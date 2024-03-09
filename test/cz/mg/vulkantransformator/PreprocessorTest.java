package cz.mg.vulkantransformator;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.macro.Macros;
import cz.mg.c.preprocessor.Preprocessor;
import cz.mg.file.File;
import cz.mg.file.FileReader;
import cz.mg.file.page.Page;
import cz.mg.file.page.PageReader;

import java.nio.file.Path;

public @Test class PreprocessorTest {
    public static void main(String[] args) {
        System.out.print("Running " + PreprocessorTest.class.getSimpleName() + " ... ");

        PreprocessorTest test = new PreprocessorTest();
        test.testClosingConditions();

        System.out.println("OK");
    }

    private final @Service PageReader pageReader = PageReader.getInstance();
    private final @Service FileReader fileReader = FileReader.getInstance();
    private final @Service Preprocessor preprocessor = Preprocessor.getInstance();

    private void testClosingConditions() {
        Path path = Path.of("test/cz/mg/vulkantransformator/test.h");

        int beginCount = 0;
        int endCount = 0;
        Page page = pageReader.read(path);
        for (String line : page.getLines()) {
            String trimLine = line.trim();

            if (trimLine.startsWith("#if")) {
                beginCount++;
            }

            if (trimLine.startsWith("#endif")) {
                endCount++;
            }
        }

        System.out.print(beginCount + " vs " + endCount + " ");

        File file = fileReader.read(path);
        preprocessor.preprocess(file, new Macros());
    }
}
