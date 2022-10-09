package cz.mg.vulkantransformator.services.parser.matcher;

import cz.mg.annotations.classes.Utility;
import cz.mg.vulkantransformator.entities.parser.code.Token;

public @Utility interface Matcher {
    boolean matches(Token token);
}
