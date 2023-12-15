package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.preprocessor.Preprocessor;
import cz.mg.c.preprocessor.processors.macro.entities.Macro;
import cz.mg.c.preprocessor.processors.macro.entities.Macros;
import cz.mg.collections.components.Capacity;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;
import cz.mg.collections.services.StringJoiner;
import cz.mg.tokenizer.entities.Position;
import cz.mg.tokenizer.entities.tokens.*;
import cz.mg.tokenizer.exceptions.CodeException;
import cz.mg.tokenizer.services.PositionService;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.parser.code.Statement;
import cz.mg.vulkantransformator.entities.parser.code.Token;
import cz.mg.vulkantransformator.entities.parser.code.TokenType;
import cz.mg.vulkantransformator.entities.parser.preprocessor.Definition;
import cz.mg.vulkantransformator.entities.vulkan.VkConstant;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.parser.segmentation.StatementParser;
import cz.mg.vulkantransformator.services.parser.vk.*;

public @Service class VulkanParser {
    private static volatile @Service VulkanParser instance;

    public static @Service VulkanParser getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new VulkanParser();
                    instance.preprocessor = Preprocessor.getInstance();
                    instance.statementParser = StatementParser.getInstance();
                    instance.parsers = new List<>(
                        VkStructureParser.getInstance(),
                        VkUnionParser.getInstance(),
                        VkEnumParser.getInstance(),
                        VkFlagsParser.getInstance(),
                        VkHandleParser.getInstance(),
                        VkTypeParser.getInstance(),
                        VkFunctionParser.getInstance(),
                        VkFunctionPointerParser.getInstance()
                    );
                    instance.stringJoiner = StringJoiner.getInstance();
                }
            }
        }
        return instance;
    }

    private static final Map<Class, TokenType> TOKEN_TYPE_MAP = new Map<>(new Capacity(20), new List<>(
        new Pair<>(WordToken.class, TokenType.NAME),
        new Pair<>(NumberToken.class, TokenType.NUMBER),
        new Pair<>(SingleQuoteToken.class, TokenType.CHARACTER),
        new Pair<>(DoubleQuoteToken.class, TokenType.STRING),
        new Pair<>(BracketToken.class, TokenType.SPECIAL),
        new Pair<>(OperatorToken.class, TokenType.SPECIAL),
        new Pair<>(SeparatorToken.class, TokenType.SPECIAL),
        new Pair<>(SpecialToken.class, TokenType.SPECIAL)
    ));

    private @Service Preprocessor preprocessor;
    private @Service StatementParser statementParser;
    private @Service List<VkParser> parsers;
    private @Service StringJoiner stringJoiner;

    private VulkanParser() {
    }

    public @Mandatory VkRoot parse(@Mandatory File file) {
        try {
            Macros macros = new Macros();
            List<Statement> statements = statementParser.parse(convert(preprocessor.preprocess(convert(file), macros)));
            VkRoot root = parseStatements(statements);
            addDefinitions(root, convert(macros));
            return root;
        } catch (CodeException e){
            Position position = PositionService.getInstance().find(convert(file).getContent(), e.getPosition());
            System.out.println("At row " + position.getRow() + " column " + position.getColumn() + ".");
            throw e;
        }
    }

    private @Mandatory VkRoot parseStatements(@Mandatory List<Statement> statements) {
        VkRoot root = new VkRoot();

        for (Statement statement : statements) {
            for (VkParser parser : parsers) {
                if (parser.matches(statement)) {
                    root.getComponents().addLast(parser.parse(statement));
                    break;
                }
            }
        }

        return root;
    }

    private void addDefinitions(@Mandatory VkRoot root, @Mandatory List<Definition> definitions) {
        for (Definition definition : definitions)
        {
            if (definition.getParameters().isEmpty() && !definition.getExpression().isEmpty())
            {
                StringBuilder value = new StringBuilder();
                for (Token expression : definition.getExpression()) {
                    value.append(expression.getText());
                }

                VkConstant constant = new VkConstant();
                constant.setName(definition.getName().getText());
                constant.setValue(value.toString());
                root.getComponents().addLast(constant);
            }
        }
    }

    private @Mandatory cz.mg.file.File convert(@Mandatory File file) {
        return new cz.mg.file.File(
            file.getPath(),
            stringJoiner.join(file.getLines(), "\n")
        );
    }

    private @Mandatory List<Definition> convert(@Mandatory Macros macros) {
        List<Definition> definitions = new List<>();
        for (Macro macro : macros.getDefinitions()) {
            definitions.addLast(convert(macro));
        }
        return definitions;
    }

    private @Mandatory Definition convert(@Mandatory Macro macro) {
        return new Definition(
            convert(macro.getName()),
            convert(macro.getParameters() != null ? macro.getParameters() : new List<>()),
            convert(macro.getTokens())
        );
    }

    private @Mandatory List<Token> convert(@Mandatory List<cz.mg.tokenizer.entities.Token> mgTokens) {
        List<Token> tokens = new List<>();
        for (cz.mg.tokenizer.entities.Token mgToken : mgTokens) {
            tokens.addLast(convert(mgToken));
        }
        return tokens;
    }

    private @Mandatory Token convert(@Mandatory cz.mg.tokenizer.entities.Token mgToken) {
        return new Token(
            null,
            mgToken.getPosition(),
            -1,
            getType(mgToken),
            mgToken.getText()
        );
    }

    private @Mandatory TokenType getType(@Mandatory cz.mg.tokenizer.entities.Token mgToken) {
        return TOKEN_TYPE_MAP.getOrCreate(mgToken.getClass(), () -> { throw new UnsupportedOperationException(); });
    }
}
