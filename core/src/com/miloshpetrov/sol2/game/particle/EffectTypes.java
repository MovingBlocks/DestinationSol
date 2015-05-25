package com.miloshpetrov.sol2.game.particle;

import java.util.HashMap;

public class EffectTypes {
  private final HashMap<String, EffectType> myTypes;

  public EffectTypes() {
    myTypes = new HashMap<String, EffectType>();
  }

  public EffectType forName(String fileName) {
    EffectType res = myTypes.get(fileName);
    if (res != null) return res;
    res = new EffectType(fileName);
    myTypes.put(fileName, res);
    return res;
  }

}
