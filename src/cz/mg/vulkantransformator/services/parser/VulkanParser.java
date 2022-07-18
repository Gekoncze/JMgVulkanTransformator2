package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.filesystem.File;
import cz.mg.vulkantransformator.entities.preprocessor.Definition;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.entities.vulkan.VkVersion;
import cz.mg.vulkantransformator.services.parser.segmentation.LineParser;
import cz.mg.vulkantransformator.services.parser.segmentation.StatementParser;
import cz.mg.vulkantransformator.services.parser.segmentation.TokenParser;
import cz.mg.vulkantransformator.services.parser.preprocessor.Preprocessor;
import cz.mg.vulkantransformator.services.parser.splicer.Splicer;
import cz.mg.vulkantransformator.services.parser.vk.*;
import cz.mg.vulkantransformator.utilities.code.Line;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.Token;

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
            instance.structureParser = VkStructureParser.getInstance();
            instance.unionParser = VkUnionParser.getInstance();
            instance.enumParser = VkEnumParser.getInstance();
            instance.flagsParser = VkFlagsParser.getInstance();
            instance.handleParser = VkHandleParser.getInstance();
            instance.typeParser = VkTypeParser.getInstance();
            instance.functionParser = VkFunctionParser.getInstance();
        }
        return instance;
    }

    private LineParser lineParser;
    private TokenParser tokenParser;
    private Splicer splicer;
    private Preprocessor preprocessor;
    private StatementParser statementParser;

    private VkStructureParser structureParser;
    private VkUnionParser unionParser;
    private VkEnumParser enumParser;
    private VkFlagsParser flagsParser;
    private VkHandleParser handleParser;
    private VkTypeParser typeParser;
    private VkFunctionParser functionParser;

    private VulkanParser() {
    }

    public @Mandatory VkRoot parse(@Mandatory VkVersion version, @Mandatory File file) {
        List<Definition> definitions = new List<>();
        List<Line> lines = lineParser.parse(file.getLines());
        List<List<Token>> linesTokens = tokenParser.parse(lines);
        List<List<Token>> joinedLinesTokens = splicer.splice(linesTokens);
        List<Token> tokens = preprocessor.preprocess(joinedLinesTokens, definitions);
        List<Statement> statements = statementParser.parse(tokens);
        return parseStatements(version, statements);
    }

    private @Mandatory VkRoot parseStatements(@Mandatory VkVersion version, @Mandatory List<Statement> statements) {
        VkRoot root = new VkRoot(version);
        root.setVersion(version);

        List<VkParser> parsers = new List<>(
            structureParser,
            unionParser,
            enumParser,
            flagsParser,
            handleParser,
            typeParser,
            functionParser
        );

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
}
