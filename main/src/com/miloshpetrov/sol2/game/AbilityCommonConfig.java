package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.TextureManager;
import com.miloshpetrov.sol2.game.particle.EffectConfig;
import com.miloshpetrov.sol2.game.particle.EffectTypes;
import com.miloshpetrov.sol2.game.sound.SolSound;
import com.miloshpetrov.sol2.game.sound.SoundManager;

public class AbilityCommonConfig {
  public final EffectConfig effect;
  public final SolSound activatedSound;

  public AbilityCommonConfig(EffectConfig effect, SolSound activatedSound) {
    this.effect = effect;
    this.activatedSound = activatedSound;
  }

  public static AbilityCommonConfig load(JsonValue node, EffectTypes types, TextureManager textureManager, GameColors cols,
    FileHandle configFile, SoundManager soundManager)
  {
    EffectConfig ec = EffectConfig.load(node.get("effect"), types, textureManager, configFile, cols);
    SolSound activatedSound = soundManager.getSound(node.getString("activatedSound"), configFile);
    return new AbilityCommonConfig(ec, activatedSound);
  }
}
