package cz.mg.vulkantransformator.services.parser.segmentation;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.Line;
import cz.mg.vulkantransformator.utilities.code.TokenType;

public @Service class TokenParser {
    private static @Optional TokenParser instance;

    public static @Mandatory TokenParser getInstance() {
        if (instance == null) {
            instance = new TokenParser();
        }
        return instance;
    }

    private TokenParser() {
    }

    public @Mandatory List<List<Token>> parse(@Mandatory List<Line> lines) {
        Boolean[] multiLineComment = new Boolean[]{ false };
        List<List<Token>> linesTokens = new List<>();

        for (Line line : lines) {
            List<Token> tokens = parse(line, multiLineComment);
            if (!tokens.isEmpty()) {
                linesTokens.addLast(tokens);
            }
        }

        return linesTokens;
    }

    private @Mandatory List<Token> parse(@Mandatory Line line, @Mandatory Boolean[] multiLineComment) {
        List<Token> tokens = new List<>();

        int start = 0;

        boolean singleQuotes = false;
        boolean doubleQuotes = false;
        boolean name = false;
        boolean number = false;

        char lch = '\0';

        for (int i = 0; i < line.getText().length(); i++) {
            char ch = line.getText().charAt(i);

            if (multiLineComment[0]) {
                if (isStar(lch) && isSlash(ch)) {
                    multiLineComment[0] = false;
                }
            } else if (singleQuotes) {
                if (!isBackslash(lch) && isSingleQuote(ch)) {
                    singleQuotes = false;
                    tokens.addLast(new Token(line, start, i + 1, TokenType.CHARACTER));
                }
            } else if (doubleQuotes) {
                if (!isBackslash(lch) && isDoubleQuote(ch)) {
                    doubleQuotes = false;
                    tokens.addLast(new Token(line, start, i + 1, TokenType.STRING));
                }
            } else if (name) {
                if (!(isUppercase(ch) || isLowercase(ch) || isNumber(ch) || isUnderscore(ch))) {
                    name = false;
                    tokens.addLast(new Token(line, start, i, TokenType.NAME));
                    i--;
                    ch = '\0';
                }
            } else if (number) {
                if (!(isNumber(ch) || isDot(ch) || isUppercase(ch) || isLowercase(ch))) {
                    number = false;
                    tokens.addLast(new Token(line, start, i, TokenType.NUMBER));
                    i--;
                    ch = '\0';
                }
            } else {
                if (isSlash(lch) && isSlash(ch)) {
                    tokens.removeLast();
                    break;
                } else if (isSlash(lch) && isStar(ch)) {
                    multiLineComment[0] = true;
                    tokens.removeLast();
                } else if (isWhitespace(ch)) {
                    // skip whitespaces
                } else if (isSingleQuote(ch)) {
                    singleQuotes = true;
                    start = i;
                } else if (isDoubleQuote(ch)) {
                    doubleQuotes = true;
                    start = i;
                } else if (isUppercase(ch) || isLowercase(ch) || isUnderscore(ch)) {
                    name = true;
                    start = i;
                } else if (isNumber(ch)) {
                    number = true;
                    start = i;
                } else {
                    tokens.addLast(new Token(line, i, i+1, TokenType.SPECIAL));
                }
            }

            lch = ch;
        }

        if (name || number) {
            tokens.addLast(new Token(line, start, line.getText().length(), name ? TokenType.NAME : TokenType.NUMBER));
        }

        if (singleQuotes || doubleQuotes) {
            throw new ParseException(line, "Missing right quote.");
        }

        return tokens;
    }

    private boolean isWhitespace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n';
    }

    private boolean isUppercase(char ch) {
        return ch >= 'A' && ch <= 'Z';
    }

    private boolean isLowercase(char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    private boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private boolean isSingleQuote(char ch) {
        return ch == '\'';
    }

    private boolean isDoubleQuote(char ch) {
        return ch == '"';
    }

    private boolean isSlash(char ch) {
        return ch == '/';
    }

    private boolean isBackslash(char ch) {
        return ch == '\\';
    }

    private boolean isUnderscore(char ch) {
        return ch == '_';
    }

    private boolean isDot(char ch) {
        return ch == '.';
    }

    private boolean isStar(char ch) {
        return ch == '*';
    }
}
