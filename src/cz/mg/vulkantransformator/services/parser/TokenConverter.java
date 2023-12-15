package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.components.Capacity;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.tokenizer.entities.tokens.*;
import cz.mg.vulkantransformator.entities.parser.code.Token;
import cz.mg.vulkantransformator.entities.parser.code.TokenType;

@Deprecated
public @Service class TokenConverter {
    private static volatile @Service TokenConverter instance;

    public static @Service TokenConverter getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TokenConverter();
                }
            }
        }
        return instance;
    }

    private static final Map<Class<?>, TokenType> TOKEN_TYPE_MAP = new Map<>(new Capacity(20), new List<>(
        new Pair<>(WordToken.class, TokenType.NAME),
        new Pair<>(NumberToken.class, TokenType.NUMBER),
        new Pair<>(SingleQuoteToken.class, TokenType.CHARACTER),
        new Pair<>(DoubleQuoteToken.class, TokenType.STRING),
        new Pair<>(BracketToken.class, TokenType.SPECIAL),
        new Pair<>(OperatorToken.class, TokenType.SPECIAL),
        new Pair<>(SeparatorToken.class, TokenType.SPECIAL),
        new Pair<>(SpecialToken.class, TokenType.SPECIAL)
    ));


    private TokenConverter() {
    }

    public @Mandatory Token convert(@Mandatory cz.mg.tokenizer.entities.Token mgToken) {
        return new Token(
            null,
            mgToken.getPosition(),
            -1,
            TOKEN_TYPE_MAP.get(mgToken.getClass()),
            mgToken.getText()
        );
    }
}
