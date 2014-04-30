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
  public final SolSound asteroidCrack;
  public final SolSound shipExplosion;
  public final SolSound burning;
  public final SolSound forceBeaconWork;
  public final SolSound doorMove;
  public final SolSound abilityRecharged;
  public final SolSound abilityRefused;
  public final SolSound controlDisabled;
  public final SolSound controlEnabled;

  public SpecialSounds(SoundMan soundMan) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "specialSounds.json");
    JsonValue node = r.parse(configFile);
    metalColl = soundMan.getSound(node.getString("metalCollision"), configFile);
    metalBulletHit = soundMan.getSound(node.getString("metalBulletHit"), configFile);
    metalEnergyHit = soundMan.getSound(node.getString("metalEnergyHit"), configFile);
    rockColl = soundMan.getSound(node.getString("rockCollision"), configFile);
    rockBulletHit = soundMan.getSound(node.getString("rockBulletHit"), configFile);
    rockEnergyHit = soundMan.getSound(node.getString("rockEnergyHit"), configFile);
    asteroidCrack = soundMan.getSound(node.getString("asteroidCrack"), configFile);
    shipExplosion = soundMan.getSound(node.getString("shipExplosion"), configFile);
    burning = soundMan.getSound(node.getString("burning"), configFile);
    burning.requireLooped();
    forceBeaconWork = soundMan.getSound(node.getString("forceBeaconWork"), configFile);
    forceBeaconWork.requireLooped();
    doorMove = soundMan.getSound(node.getString("doorMove"), configFile);
    abilityRecharged = soundMan.getSound(node.getString("abilityRecharged"), configFile);
    abilityRefused = soundMan.getSound(node.getString("abilityRefused"), configFile);
    controlDisabled = soundMan.getSound(node.getString("controlDisabled"), configFile);
    controlEnabled = soundMan.getSound(node.getString("controlEnabled"), configFile);
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
    game.getSoundMan().play(game, sound, pos, o);
  }

  public void playColl(SolGame game, float absImpulse, SolObj o, Vector2 pos) {
    if (o == null || absImpulse < .1f) return;
    Boolean metal = o.isMetal();
    if (metal == null) return;
    game.getSoundMan().play(game, metal ? metalColl : rockColl, pos, o, absImpulse * Const.IMPULSE_TO_COLL_VOL);
  }
}
