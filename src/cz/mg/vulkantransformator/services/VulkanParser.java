package cz.mg.vulkantransformator.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.vulkantransformator.entities.preprocessor.Definition;
import cz.mg.vulkantransformator.entities.vulkan.VkRoot;
import cz.mg.vulkantransformator.entities.vulkan.VkVersion;
import cz.mg.vulkantransformator.services.parser.LineParser;
import cz.mg.vulkantransformator.services.parser.StatementParser;
import cz.mg.vulkantransformator.services.parser.TokenParser;
import cz.mg.vulkantransformator.services.preprocessor.Preprocessor;
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
            instance.preprocessor = Preprocessor.getInstance();
            instance.statementParser = StatementParser.getInstance();
            instance.structureParser = VkStructureParser.getInstance();
        }
        return instance;
    }

    private LineParser lineParser;
    private TokenParser tokenParser;
    private Preprocessor preprocessor;
    private StatementParser statementParser;
    private VkStructureParser structureParser;

    private VulkanParser() {
    }

    public @Mandatory VkRoot parse(@Mandatory VkVersion version, @Mandatory List<String> stringLines) {
        VkRoot root = new VkRoot(version);
        root.setVersion(version);

        List<Definition> definitions = new List<>();
        List<Line> lines = lineParser.parse(stringLines);
        List<List<Token>> linesTokens = tokenParser.parse(lines);
        List<Token> tokens = preprocessor.preprocess(linesTokens, definitions);
        List<Statement> statements = statementParser.parse(tokens);

        root.getComponents().addCollectionLast(structureParser.parse(statements));

        return root;
    }
}
