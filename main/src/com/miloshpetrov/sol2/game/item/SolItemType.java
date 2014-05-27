package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.common.ColUtil;
import com.miloshpetrov.sol2.game.sound.SolSound;
import com.miloshpetrov.sol2.game.sound.SoundMan;

public class SolItemType {
  public final Color color;
  public final SolSound pickUpSound;

  public SolItemType(Color color, SolSound pickUpSound) {
    this.color = color;
    this.pickUpSound = pickUpSound;
  }

}
