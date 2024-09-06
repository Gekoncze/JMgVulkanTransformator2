package cz.mg.vulkantransformator.services.converter.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.components.StringJoiner;
import cz.mg.collections.list.List;
import cz.mg.token.Token;

public @Service class NumberParser {
    private static volatile @Service NumberParser instance;

    public static @Service NumberParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new NumberParser();
                }
            }
        }
        return instance;
    }

    private NumberParser() {
    }

    public int parse(@Mandatory List<Token> expression) {
        return parse(new StringJoiner<>(expression).withConverter(Token::getText).join());
    }

    public int parse(@Mandatory String s) {
        String number = s.substring(0, findNumberEnd(s) + 1);
        return Integer.parseInt(number);
    }

    private int findNumberEnd(@Mandatory String number) {
        int i = number.length() - 1;
        while (i >= 0) {
            char ch = number.charAt(i);
            if (ch == 'L' || ch == 'U') {
                i--;
            } else {
                break;
            }
        }
        return i;
    }
}
