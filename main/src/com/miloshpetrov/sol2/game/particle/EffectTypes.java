package com.miloshpetrov.sol2.game.particle;

import java.util.HashMap;

public class EffectTypes {
    private final HashMap<String, EffectType> myTypes;

    public EffectTypes() {
        myTypes = new HashMap<String, EffectType>();
    }

    public EffectType forName(String fileName) {
        EffectType result = myTypes.get(fileName);

        if (result == null) {
            result = new EffectType(fileName);
            myTypes.put(fileName, result);
        }

        return result;
    }
}
