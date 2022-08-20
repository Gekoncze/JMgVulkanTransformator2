package cz.mg.vulkantransformator.services.translator.vk;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.collections.list.List;
import cz.mg.collections.map.Map;
import cz.mg.collections.pair.Pair;

public @Service class TypenameTranslator {
    private static @Optional TypenameTranslator instance;

    public static @Mandatory TypenameTranslator getInstance() {
        if (instance == null) {
            instance = new TypenameTranslator();
        }
        return instance;
    }

    private static final Map<String, String> map = new Map<>(
        50,
        new List<>(
            new Pair<>("void", "CObject"),
            new Pair<>("char", "CChar"),
            new Pair<>("uint8_t", "CUInt8"),
            new Pair<>("uint16_t", "CUInt16"),
            new Pair<>("uint32_t", "CUInt32"),
            new Pair<>("uint64_t", "CUInt64"),
            new Pair<>("int8_t", "CInt8"),
            new Pair<>("int16_t", "CInt16"),
            new Pair<>("int32_t", "CInt32"),
            new Pair<>("int64_t", "CInt64"),
            new Pair<>("float", "CFloat"),
            new Pair<>("double", "CDouble"),
            new Pair<>("int", "CInt32"),
            new Pair<>("size_t", "CSize")
        )
    );

    private TypenameTranslator() {
    }

    public @Mandatory String translate(@Mandatory String typename) {
        String newTypename = map.getOptional(typename);

        if (newTypename != null) {
            return newTypename;
        } else {
            return typename;
        }
    }
}
