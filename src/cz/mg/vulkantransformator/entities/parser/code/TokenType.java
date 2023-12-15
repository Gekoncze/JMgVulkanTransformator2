package cz.mg.vulkantransformator.entities.parser.code;

import cz.mg.annotations.classes.Entity;

@Deprecated
public @Entity enum TokenType {
    NAME,
    NUMBER,
    CHARACTER,
    STRING,
    SPECIAL
}
