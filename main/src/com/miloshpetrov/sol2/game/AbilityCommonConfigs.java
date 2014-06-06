package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.game.particle.EffectTypes;
import com.miloshpetrov.sol2.game.sound.SoundMan;

public class AbilityCommonConfigs {
  public final AbilityCommonConfig teleport;
  public final AbilityCommonConfig emWave;
  public final AbilityCommonConfig unShield;
  public final AbilityCommonConfig knockBack;
  public final AbilityCommonConfig sloMo;

  public AbilityCommonConfigs(EffectTypes effectTypes, TexMan texMan, GameCols cols, SoundMan soundMan) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "abilities.json");
    JsonValue node = r.parse(configFile);
    teleport = AbilityCommonConfig.load(node.get("teleport"), effectTypes, texMan, cols, configFile, soundMan);
    emWave = AbilityCommonConfig.load(node.get("emWave"), effectTypes, texMan, cols, configFile, soundMan);
    unShield = AbilityCommonConfig.load(node.get("unShield"), effectTypes, texMan, cols, configFile, soundMan);
    knockBack = AbilityCommonConfig.load(node.get("knockBack"), effectTypes, texMan, cols, configFile, soundMan);
    sloMo = AbilityCommonConfig.load(node.get("sloMo"), effectTypes, texMan, cols, configFile, soundMan);
  }
}
