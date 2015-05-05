package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.files.FileManager;
import com.miloshpetrov.sol2.game.particle.EffectTypes;
import com.miloshpetrov.sol2.game.ship.AbilityConfig;
import com.miloshpetrov.sol2.game.sound.SoundManager;

public class AbilityCommonConfigs {
  public final AbilityCommonConfig teleport;
  public final AbilityCommonConfig emWave;
  public final AbilityCommonConfig unShield;
  public final AbilityCommonConfig knockBack;
  public final AbilityCommonConfig sloMo;

  public static AbilityCommonConfigs getInstance() {
    if(instance == null) {
        instance =  new AbilityCommonConfigs(EffectTypes.getInstance(), TextureManager.getInstance(), GameColors.getInstance(), SoundManager.getInstance());
    }
    return instance;
  }

  public AbilityCommonConfigs(EffectTypes effectTypes, TextureManager textureManager, GameColors cols, SoundManager soundManager) {
    JsonReader r = new JsonReader();

    FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("abilities.json");
    JsonValue node = r.parse(configFile);
    teleport = AbilityCommonConfig.load(node.get("teleport"), effectTypes, textureManager, cols, configFile, soundManager);
    emWave = AbilityCommonConfig.load(node.get("emWave"), effectTypes, textureManager, cols, configFile, soundManager);
    unShield = AbilityCommonConfig.load(node.get("unShield"), effectTypes, textureManager, cols, configFile, soundManager);
    knockBack = AbilityCommonConfig.load(node.get("knockBack"), effectTypes, textureManager, cols, configFile, soundManager);
    sloMo = AbilityCommonConfig.load(node.get("sloMo"), effectTypes, textureManager, cols, configFile, soundManager);
  }

  private static AbilityCommonConfigs instance = null;
}
