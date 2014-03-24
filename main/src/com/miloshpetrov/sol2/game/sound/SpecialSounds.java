package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.game.*;

public class SpecialSounds {

  public final SolSound metalColl;
  public final SolSound metalBulletHit;
  public final SolSound metalEnergyHit;
  public final SolSound rockColl;
  public final SolSound rockBulletHit;
  public final SolSound rockEnergyHit;
  public final SolSound asteroidSplit;
  public final SolSound shipExplosion;

  public SpecialSounds(SoundMan soundMan) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "specialSounds.json");
    JsonValue node = r.parse(configFile);
    metalColl = getSound("metalCollision", soundMan, configFile, node);
    metalBulletHit = getSound("metalBulletHit", soundMan, configFile, node);
    metalEnergyHit = getSound("metalEnergyHit", soundMan, configFile, node);
    rockColl = getSound("rockCollision", soundMan, configFile, node);
    rockBulletHit = getSound("rockBulletHit", soundMan, configFile, node);
    rockEnergyHit = getSound("rockEnergyHit", soundMan, configFile, node);
    asteroidSplit = getSound("asteroidSplit", soundMan, configFile, node);
    shipExplosion = getSound("shipExplosion", soundMan, configFile, node);
  }

  private SolSound getSound(String paramName, SoundMan soundMan, FileHandle configFile, JsonValue node) {
    String dir = node.getString(paramName);
    return soundMan.getSound(dir, configFile);
  }

  public SolSound dmgSound(boolean forMetal, DmgType dmgType) {
    if (dmgType == DmgType.ENERGY) {
      return forMetal ? metalEnergyHit : rockEnergyHit;
    }
    if (dmgType == DmgType.BULLET) {
      return forMetal ? metalBulletHit : rockBulletHit;
    }
    return null;
  }

  public void playDmg(SolGame game, SolObj o, Vector2 pos, DmgType dmgType) {
    if (o == null) return;
    Boolean metal = o.isMetal();
    if (metal == null) return;
    SolSound sound = dmgSound(metal, dmgType);
    if (sound == null) return;
    game.getSoundMan().play(game, sound, pos, null);
  }

  public void playColl(SolGame game, float absImpulse, SolObj o, Vector2 pos) {
    if (o == null || absImpulse < .1f) return;
    Boolean metal = o.isMetal();
    if (metal == null) return;
    game.getSoundMan().play(game, metal ? metalColl : rockColl, pos, null, absImpulse * Const.IMPULSE_TO_COLL_VOL);
  }
}
