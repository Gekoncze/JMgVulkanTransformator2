package cz.mg.vulkantransformator.services.parser.matcher;

import cz.mg.annotations.classes.Utility;
import cz.mg.vulkantransformator.utilities.code.Token;

public @Utility interface Matcher {
    boolean matches(Token token);
}
