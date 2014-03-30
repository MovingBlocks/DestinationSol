package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.game.dra.DraLevel;

import java.util.ArrayList;
import java.util.List;

public class SpecialEffects {

  private final EffectConfig mySmoke;
  private final EffectConfig myFire;

  public SpecialEffects(EffectTypes effectTypes, TexMan texMan) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "specialEffects.json");
    JsonValue node = r.parse(configFile);
    JsonValue smokeNode = node.get("smoke");
    mySmoke = EffectConfig.load(smokeNode, effectTypes, texMan, configFile);
    JsonValue fireNode = node.get("fire");
    myFire = EffectConfig.load(fireNode, effectTypes, texMan, configFile);
  }

  public List<ParticleSrc> buildFireSmoke(float objSz) {
    ArrayList<ParticleSrc> res = new ArrayList<ParticleSrc>();
    float sz = objSz * .3f;
    ParticleSrc smoke = new ParticleSrc(mySmoke.effectType, sz, DraLevel.PART_FG_0, new Vector2(), mySmoke.tex);
    res.add(smoke);
    ParticleSrc fire = new ParticleSrc(myFire.effectType, sz, DraLevel.PART_FG_1, new Vector2(), myFire.tex);
    res.add(fire);
    return res;
  }
}
