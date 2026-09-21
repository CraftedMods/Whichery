package com.supersouper.whichery.common.entity.demon;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum EnumPossessedAnimalBehavior {

    BERSERK("berserk"), MIMIC_NORMAL("mimic_normal"), WEIRD("weird");

    private final String nbtName;

    static final Map<String, EnumPossessedAnimalBehavior> VALUES_BY_NBT_NAME;

    static {
        VALUES_BY_NBT_NAME = Collections.unmodifiableMap(
            Arrays.stream(values())
                .collect(Collectors.toMap(EnumPossessedAnimalBehavior::getNbtName, Function.identity()))
        );
    }

    EnumPossessedAnimalBehavior(String nbtName) {
        this.nbtName = nbtName;
    }

    public String getNbtName() {
        return nbtName;
    }

    public static EnumPossessedAnimalBehavior deserializeFromNbt(String nbtName) {
        return VALUES_BY_NBT_NAME.get(nbtName);
    }
}
