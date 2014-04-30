package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.game.particle.EffectTypes;

public class AbilityCommonConfigs {
  public final AbilityCommonConfig teleport;
  public final AbilityCommonConfig emWave;
  public final AbilityCommonConfig unShield;
  public final AbilityCommonConfig knockBack;
  public final AbilityCommonConfig sloMo;

  public AbilityCommonConfigs(EffectTypes effectTypes, TexMan texMan, GameCols cols) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "abilities.json");
    JsonValue node = r.parse(configFile);
    teleport = null;
    emWave = AbilityCommonConfig.load(node.get("emWave"), effectTypes, texMan, cols, configFile);
    unShield = AbilityCommonConfig.load(node.get("unShield"), effectTypes, texMan, cols, configFile);
    knockBack = AbilityCommonConfig.load(node.get("knockBack"), effectTypes, texMan, cols, configFile);
    sloMo = AbilityCommonConfig.load(node.get("sloMo"), effectTypes, texMan, cols, configFile);
  }
}
