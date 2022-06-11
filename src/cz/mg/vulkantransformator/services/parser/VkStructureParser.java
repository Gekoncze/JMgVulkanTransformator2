package cz.mg.vulkantransformator.services.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.collections.list.List;
import cz.mg.collections.list.ListItem;
import cz.mg.vulkantransformator.entities.vulkan.VkField;
import cz.mg.vulkantransformator.entities.vulkan.VkStructure;
import cz.mg.vulkantransformator.services.parser.matcher.Matchers;
import cz.mg.vulkantransformator.services.parser.matcher.PatternMatcher;
import cz.mg.vulkantransformator.services.parser.other.ParseException;
import cz.mg.vulkantransformator.services.parser.other.TokenRemover;
import cz.mg.vulkantransformator.services.parser.segmentation.StatementParser;
import cz.mg.vulkantransformator.utilities.code.Statement;
import cz.mg.vulkantransformator.utilities.code.Token;
import cz.mg.vulkantransformator.utilities.code.TokenType;

public @Service class VkStructureParser { // TODO - add test
    private static VkStructureParser instance;

    public static @Mandatory VkStructureParser getInstance() {
        if (instance == null) {
            instance = new VkStructureParser();
            instance.patternMatcher = PatternMatcher.getInstance();
            instance.statementParser = StatementParser.getInstance();
            instance.tokenRemover = TokenRemover.getInstance();
        }
        return instance;
    }

    private PatternMatcher patternMatcher;
    private StatementParser statementParser;
    private TokenRemover tokenRemover;

    private VkStructureParser() {
    }

    /**
     * Example:
     *
     * typedef struct VkPipelineColorBlendStateCreateInfo {
     *     VkStructureType                               sType;
     *     const void*                                   pNext;
     *     VkPipelineColorBlendStateCreateFlags          flags;
     *     VkBool32                                      logicOpEnable;
     *     VkLogicOp                                     logicOp;
     *     uint32_t                                      attachmentCount;
     *     const VkPipelineColorBlendAttachmentState*    pAttachments;
     *     float                                         blendConstants[4];
     * } VkPipelineColorBlendStateCreateInfo;
     */
    public @Mandatory List<VkStructure> parse(@Mandatory List<Statement> statements) {
        List<VkStructure> structures = new List<>();

        for (Statement statement : statements) {
           if (patternMatcher.matches(
               statement,
               Matchers.text("typedef"),
               Matchers.text("struct"),
               Matchers.any(),
               Matchers.text("{")
           )) {
               structures.addLast(parseStructure(statement));
           }
        }

        return structures;
    }

    private @Mandatory VkStructure parseStructure(@Mandatory Statement statement) {
        List<Token> tokens = new List<>(statement.getTokens());

        tokenRemover.removeFirst(tokens, "typedef");
        tokenRemover.removeFirst(tokens, "struct");

        VkStructure structure = new VkStructure(tokenRemover.removeFirst(tokens, TokenType.NAME).getText());

        tokenRemover.removeFirst(tokens, "{");

        tokenRemover.removeLast(tokens, structure.getName());
        tokenRemover.removeLast(tokens, "}");

        List<Statement> fieldStatements = statementParser.parse(tokens);

        for (Statement fieldStatement : fieldStatements) {
            structure.getFields().addLast(parseField(fieldStatement));
        }

        return structure;
    }

    private @Mandatory VkField parseField(@Mandatory Statement statement) {
        List<Token> tokens = new List<>(statement.getTokens());

        removeConstTokens(tokens);
        removeStructTokens(tokens);

        int pointers = removePointerTokens(tokens);
        int array = removeArrayTokens(tokens);
        String typename = tokenRemover.removeFirst(tokens, TokenType.NAME).getText();
        String name = tokenRemover.removeFirst(tokens, TokenType.NAME).getText();

        if (!tokens.isEmpty()) {
            throw new ParseException(
                tokens.getFirst(),
                "Unprocessed structure field token '" + tokens.getFirst().getText() + "'."
            );
        }

        return new VkField(typename, pointers, name, array);
    }

    private void removeConstTokens(@Mandatory List<Token> tokens) {
        ListItem<Token> item = tokens.getFirstItem();
        while (item != null) {
            ListItem<Token> nextItem = item.getNextItem();
            if (item.get().getText().equals("const")) {
                tokens.remove(item);
            }
            item = nextItem;
        }
    }

    private void removeStructTokens(@Mandatory List<Token> tokens) {
        ListItem<Token> item = tokens.getFirstItem();
        while (item != null) {
            ListItem<Token> nextItem = item.getNextItem();
            if (item.get().getText().equals("struct")) {
                tokens.remove(item);
            }
            item = nextItem;
        }
    }

    private int removePointerTokens(@Mandatory List<Token> tokens) {
        ListItem<Token> item = tokens.getFirstItem();
        int count = 0;
        while (item != null) {
            ListItem<Token> nextItem = item.getNextItem();
            if (item.get().getText().equals("*")) {
                tokens.remove(item);
                count++;
            }
            item = nextItem;
        }
        return count;
    }

    private int removeArrayTokens(@Mandatory List<Token> tokens) {
        ListItem<Token> openingItem = null;
        ListItem<Token> closingItem = null;

        for (ListItem<Token> item = tokens.getFirstItem(); item != null; item = item.getNextItem()) {
            if (item.get().getText().equals("[")) {
                if (openingItem == null) {
                    openingItem = item;
                } else {
                    throw new ParseException(item.get(), "Too many opening array brackets.");
                }
            }

            if (item.get().getText().equals("]")) {
                if (closingItem == null) {
                    closingItem = item;
                } else {
                    throw new ParseException(item.get(), "Too many closing array brackets.");
                }
            }
        }

        if (openingItem != null && closingItem != null) {
            // TODO - this condition might be illegal
            if (openingItem.getNextItem() != closingItem.getPreviousItem()) {
                throw new IllegalArgumentException("Illegal array declaration."); // TODO - use parse exception
            }
            ListItem<Token> numberItem = openingItem.getNextItem();
            tokens.remove(openingItem);
            tokens.remove(numberItem);
            tokens.remove(closingItem);
            return Integer.parseInt(numberItem.get().getText());
        } else if (openingItem != null) {
            throw new ParseException(openingItem.get(), "Missing right array bracket.");
        } else if (closingItem != null) {
            throw new ParseException(closingItem.get(), "Missing left array bracket.");
        } else {
            return 0;
        }
    }
}
