package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.particle.EffectConfig;
import com.miloshpetrov.sol2.game.particle.EffectTypes;
import com.miloshpetrov.sol2.game.sound.SolSound;
import com.miloshpetrov.sol2.game.sound.SoundMan;

public class AbilityCommonConfig {
  public final EffectConfig effect;
  public final SolSound activatedSound;

  public AbilityCommonConfig(EffectConfig effect, SolSound activatedSound) {
    this.effect = effect;
    this.activatedSound = activatedSound;
  }

  public static AbilityCommonConfig load(JsonValue node, EffectTypes types, TexMan texMan, GameCols cols,
    FileHandle configFile, SoundMan soundMan)
  {
    EffectConfig ec = EffectConfig.load(node.get("effect"), types, texMan, configFile, cols);
    SolSound activatedSound = soundMan.getSound(node.getString("activatedSound"), configFile);
    return new AbilityCommonConfig(ec, activatedSound);
  }
}
