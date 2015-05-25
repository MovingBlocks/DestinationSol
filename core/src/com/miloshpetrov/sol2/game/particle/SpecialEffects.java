package com.miloshpetrov.sol2.game.particle;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.files.FileManager;
import com.miloshpetrov.sol2.game.GameCols;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.dra.DraLevel;

import java.util.ArrayList;
import java.util.List;

public class SpecialEffects {

  private final EffectConfig mySmoke;
  private final EffectConfig myFire;
  private final EffectConfig myElectricity;
  private final EffectConfig myShipExplSmoke;
  private final EffectConfig myShipExplFire;
  private final EffectConfig myAsteroidDust;
  private final EffectConfig myForceBeacon;
  public final EffectConfig starPortFlow;
  public final EffectConfig transcendentWork;

  public SpecialEffects(EffectTypes effectTypes, TextureManager textureManager, GameCols cols) {
    JsonReader r = new JsonReader();
    FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("specialEffects.json");
    JsonValue node = r.parse(configFile);
    mySmoke = EffectConfig.load(node.get("smoke"), effectTypes, textureManager, configFile, cols);
    myFire = EffectConfig.load(node.get("fire"), effectTypes, textureManager, configFile, cols);
    myElectricity = EffectConfig.load(node.get("electricity"), effectTypes, textureManager, configFile, cols);
    myShipExplSmoke = EffectConfig.load(node.get("shipExplosionSmoke"), effectTypes, textureManager, configFile, cols);
    myShipExplFire = EffectConfig.load(node.get("shipExplosionFire"), effectTypes, textureManager, configFile, cols);
    myAsteroidDust = EffectConfig.load(node.get("asteroidDust"), effectTypes, textureManager, configFile, cols);
    myForceBeacon = EffectConfig.load(node.get("forceBeacon"), effectTypes, textureManager, configFile, cols);
    starPortFlow = EffectConfig.load(node.get("starPortFlow"), effectTypes, textureManager, configFile, cols);
    transcendentWork = EffectConfig.load(node.get("transcendentWork"), effectTypes, textureManager, configFile, cols);
  }

  public List<ParticleSrc> buildBodyEffs(float objRad, SolGame game, Vector2 pos, Vector2 spd) {
    ArrayList<ParticleSrc> res = new ArrayList<ParticleSrc>();
    float sz = objRad * .9f;
    ParticleSrc smoke = new ParticleSrc(mySmoke, sz, DraLevel.PART_FG_0, new Vector2(), true, game, pos, spd, 0);
    res.add(smoke);
    ParticleSrc fire = new ParticleSrc(myFire, sz, DraLevel.PART_FG_1, new Vector2(), true, game, pos, spd, 0);
    res.add(fire);
    ParticleSrc elec = new ParticleSrc(myElectricity, objRad * 1.2f, DraLevel.PART_FG_0, new Vector2(), true, game, pos, spd, 0);
    res.add(elec);
    return res;
  }

  public void explodeShip(SolGame game, Vector2 pos, float sz) {
    PartMan pm = game.getPartMan();
    ParticleSrc smoke = new ParticleSrc(myShipExplSmoke, 2 * sz, DraLevel.PART_FG_0, new Vector2(), false, game, pos, Vector2.Zero, 0);
    pm.finish(game, smoke, pos);
    ParticleSrc fire = new ParticleSrc(myShipExplFire, .7f * sz, DraLevel.PART_FG_1, new Vector2(), false, game, pos, Vector2.Zero, 0);
    pm.finish(game, fire, pos);
    pm.blinks(pos, game, sz);
  }

  public void asteroidDust(SolGame game, Vector2 pos, Vector2 spd, float size) {
    PartMan pm = game.getPartMan();
    ParticleSrc smoke = new ParticleSrc(myAsteroidDust, size, DraLevel.PART_FG_0, new Vector2(), true, game, pos, spd, 0);
    pm.finish(game, smoke, pos);
  }

  public ParticleSrc buildForceBeacon(float sz, SolGame game, Vector2 relPos, Vector2 basePos, Vector2 spd) {
    return new ParticleSrc(myForceBeacon, sz, DraLevel.PART_FG_0, relPos, false, game, basePos, spd, 0);
  }
}
