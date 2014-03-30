package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.dra.DraLevel;

import java.util.ArrayList;
import java.util.List;

public class SpecialEffects {

  private final EffectConfig mySmoke;
  private final EffectConfig myFire;
  private final EffectConfig myShipExplSmoke;
  private final EffectConfig myShipExplFire;

  public SpecialEffects(EffectTypes effectTypes, TexMan texMan) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "specialEffects.json");
    JsonValue node = r.parse(configFile);
    JsonValue smokeNode = node.get("smoke");
    mySmoke = EffectConfig.load(smokeNode, effectTypes, texMan, configFile);
    myFire = EffectConfig.load(node.get("fire"), effectTypes, texMan, configFile);
    myShipExplSmoke = EffectConfig.load(node.get("shipExplosionSmoke"), effectTypes, texMan, configFile);
    myShipExplFire = EffectConfig.load(node.get("shipExplosionFire"), effectTypes, texMan, configFile);
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

  public void explodeShip(SolGame game, Vector2 pos, float sz) {
    PartMan pm = game.getPartMan();
    ParticleSrc smoke = new ParticleSrc(myShipExplSmoke.effectType, sz, DraLevel.PART_FG_0, new Vector2(), myShipExplSmoke.tex);
    pm.finish(game, smoke, pos);
    ParticleSrc fire = new ParticleSrc(myShipExplFire.effectType, .5f * sz, DraLevel.PART_FG_1, new Vector2(), myShipExplFire.tex);
    pm.finish(game, fire, pos);
    pm.blinks(pos, game, .3f * sz);
  }
}
