package cz.mg.vulkantransformator.services.parser.preprocessor;

import cz.mg.annotations.classes.Utility;

public @Utility class Directives {
    public static final String INCLUDE = "include";
    public static final String IF = "if";
    public static final String ELIF = "elif";
    public static final String ELSE = "else";
    public static final String IFDEF = "ifdef";
    public static final String IFNDEF = "ifndef";
    public static final String DEFINE = "define";
    public static final String UNDEF = "undef";
    public static final String ENDIF = "endif";
    public static final String ERROR = "error";
}
