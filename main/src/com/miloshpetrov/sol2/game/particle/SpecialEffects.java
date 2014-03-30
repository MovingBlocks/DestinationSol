package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.game.dra.DraLevel;

public class SpecialEffects {

  private final EffectConfig mySmoke;

  public SpecialEffects(EffectTypes effectTypes, TexMan texMan) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "specialEffects.json");
    JsonValue node = r.parse(configFile);
    JsonValue smokeNode = node.get("smoke");
    mySmoke = EffectConfig.load(smokeNode, effectTypes, texMan, configFile);
  }

  public ParticleSrc buildSmoke(Vector2 relPos, float size) {
    return new ParticleSrc(mySmoke.effectType, size, DraLevel.PART_FG_0, relPos, mySmoke.tex);
  }
}
