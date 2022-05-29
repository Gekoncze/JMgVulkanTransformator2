package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.Token;

public @Service class PatternMatcher {
    private static @Optional PatternMatcher instance;

    public static @Mandatory PatternMatcher getInstance() {
        if (instance == null) {
            instance = new PatternMatcher();
        }
        return instance;
    }

    private PatternMatcher() {
    }

    public boolean matches(@Mandatory Statement statement, @Mandatory Matcher... matchers) {
        if (statement.getTokens().count() >= matchers.length) {
            int i = 0;
            for (Token token : statement.getTokens()) {
                if (i >= matchers.length) {
                    break;
                }

                if (!matchers[i].matches(token)) {
                    return false;
                }
                i++;
            }
            return true;
        } else {
            return false;
        }
    }
}
