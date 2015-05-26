package org.destinationsol.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.sound.SolSound;
import org.destinationsol.game.sound.SoundManager;

public class AbilityCommonConfig {
  public final EffectConfig effect;
  public final SolSound activatedSound;

  public AbilityCommonConfig(EffectConfig effect, SolSound activatedSound) {
    this.effect = effect;
    this.activatedSound = activatedSound;
  }

  public static AbilityCommonConfig load(JsonValue node, EffectTypes types, TextureManager textureManager, GameCols cols,
    FileHandle configFile, SoundManager soundManager)
  {
    EffectConfig ec = EffectConfig.load(node.get("effect"), types, textureManager, configFile, cols);
    SolSound activatedSound = soundManager.getSound(node.getString("activatedSound"), configFile);
    return new AbilityCommonConfig(ec, activatedSound);
  }
}
