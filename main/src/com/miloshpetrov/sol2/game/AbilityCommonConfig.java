package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.particle.EffectConfig;
import com.miloshpetrov.sol2.game.particle.EffectTypes;

public class AbilityCommonConfig {
  public final EffectConfig effect;

  public AbilityCommonConfig(EffectConfig effect) {
    this.effect = effect;
  }

  public static AbilityCommonConfig load(JsonValue node, EffectTypes types, TexMan texMan, GameCols cols,
    FileHandle configFile)
  {
    EffectConfig ec = EffectConfig.load(node.get("effect"), types, texMan, configFile, cols);
    return new AbilityCommonConfig(ec);
  }
}
