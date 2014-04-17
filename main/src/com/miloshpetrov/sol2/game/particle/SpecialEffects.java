package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.game.GameCols;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.dra.DraLevel;

import java.util.ArrayList;
import java.util.List;

public class SpecialEffects {

  private final EffectConfig mySmoke;
  private final EffectConfig myFire;
  private final EffectConfig myShipExplSmoke;
  private final EffectConfig myShipExplFire;

  public SpecialEffects(EffectTypes effectTypes, TexMan texMan, GameCols cols) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "specialEffects.json");
    JsonValue node = r.parse(configFile);
    mySmoke = EffectConfig.load(node.get("smoke"), effectTypes, texMan, configFile, cols);
    myFire = EffectConfig.load(node.get("fire"), effectTypes, texMan, configFile, cols);
    myShipExplSmoke = EffectConfig.load(node.get("shipExplosionSmoke"), effectTypes, texMan, configFile, cols);
    myShipExplFire = EffectConfig.load(node.get("shipExplosionFire"), effectTypes, texMan, configFile, cols);
  }

  public List<ParticleSrc> buildFireSmoke(float objSz, SolGame game, Vector2 pos, Vector2 spd) {
    ArrayList<ParticleSrc> res = new ArrayList<ParticleSrc>();
    float sz = objSz * .3f;
    ParticleSrc smoke = new ParticleSrc(mySmoke, sz, DraLevel.PART_FG_0, new Vector2(), true, game, pos, spd);
    res.add(smoke);
    ParticleSrc fire = new ParticleSrc(myFire, sz, DraLevel.PART_FG_1, new Vector2(), true, game, pos, spd);
    res.add(fire);
    return res;
  }

  public void explodeShip(SolGame game, Vector2 pos, float sz) {
    PartMan pm = game.getPartMan();
    ParticleSrc smoke = new ParticleSrc(myShipExplSmoke, 2 * sz, DraLevel.PART_FG_0, new Vector2(), false, game, pos, Vector2.Zero);
    pm.finish(game, smoke, pos);
    ParticleSrc fire = new ParticleSrc(myShipExplFire, .7f * sz, DraLevel.PART_FG_1, new Vector2(), false, game, pos, Vector2.Zero);
    pm.finish(game, fire, pos);
    pm.blinks(pos, game, sz);
  }
}
