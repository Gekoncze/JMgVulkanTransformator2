package cz.mg.vulkantransformator.services.parser.matcher;

import cz.mg.annotations.classes.Component;
import cz.mg.vulkantransformator.entities.parser.code.Token;

public @Component interface Matcher {
    boolean matches(Token token);
}
