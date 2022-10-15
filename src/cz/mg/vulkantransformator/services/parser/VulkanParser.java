package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.parser.preprocessor.Definition;
import cz.mg.vulkantransformator.entities.vulkan.VkConstant;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.services.parser.segmentation.LineParser;
import cz.mg.vulkantransformator.services.parser.segmentation.StatementParser;
import cz.mg.vulkantransformator.services.parser.segmentation.TokenParser;
import cz.mg.vulkantransformator.services.parser.preprocessor.Preprocessor;
import cz.mg.vulkantransformator.services.parser.splicer.Splicer;
import cz.mg.vulkantransformator.services.parser.vk.*;
import cz.mg.vulkantransformator.entities.parser.code.Line;
import cz.mg.vulkantransformator.entities.parser.code.Statement;
import cz.mg.vulkantransformator.entities.parser.code.Token;

public @Service class VulkanParser {
    private static VulkanParser instance;

    public static @Mandatory VulkanParser getInstance() {
        if (instance == null) {
            instance = new VulkanParser();
            instance.lineParser = LineParser.getInstance();
            instance.tokenParser = TokenParser.getInstance();
            instance.splicer = Splicer.getInstance();
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
        }
        return instance;
    }

    private LineParser lineParser;
    private TokenParser tokenParser;
    private Splicer splicer;
    private Preprocessor preprocessor;
    private StatementParser statementParser;
    private List<VkParser> parsers;

    private VulkanParser() {
    }

    public @Mandatory VkRoot parse(@Mandatory File file) {
        List<Definition> definitions = new List<>();
        List<Line> lines = lineParser.parse(file.getLines());
        List<List<Token>> linesTokens = tokenParser.parse(lines);
        List<List<Token>> joinedLinesTokens = splicer.splice(linesTokens);
        List<Token> tokens = preprocessor.preprocess(joinedLinesTokens, definitions);
        List<Statement> statements = statementParser.parse(tokens);
        VkRoot root = parseStatements(statements);
        addDefinitions(root, definitions);
        return root;
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
                for (Token expression : definition.getExpression())
                {
                    value.append(expression.getText());
                }

                VkConstant constant = new VkConstant();
                constant.setName(definition.getName().getText());
                constant.setValue(value.toString());
                root.getComponents().addLast(constant);
            }
        }
    }
}
