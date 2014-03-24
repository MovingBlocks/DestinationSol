package com.miloshpetrov.sol2.game.sound;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.SolFiles;

public class SpecialSounds {

  public final SolSound metalColl;
  public final SolSound rockColl;
  public final SolSound asteroidSplit;

  public SpecialSounds(SoundMan soundMan) {
    JsonReader r = new JsonReader();
    FileHandle configFile = SolFiles.readOnly(Const.CONFIGS_DIR + "specialSounds.json");
    JsonValue node = r.parse(configFile);
    metalColl = getSound("metalCollision", soundMan, configFile, node);
    rockColl = getSound("rockCollision", soundMan, configFile, node);
    asteroidSplit = getSound("asteroidSplit", soundMan, configFile, node);
  }

  private SolSound getSound(String paramName, SoundMan soundMan, FileHandle configFile, JsonValue node) {
    String dir = node.getString(paramName);
    return soundMan.getSound(dir, configFile);
  }
}
