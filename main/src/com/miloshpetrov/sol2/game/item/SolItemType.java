package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.Color;
import com.miloshpetrov.sol2.game.sound.SolSound;

public class SolItemType {
  public final Color color;
  public final SolSound pickUpSound;
  public final Color uiColor;

  public SolItemType(Color color, SolSound pickUpSound) {
    this.color = color;
    uiColor = new Color(color);
    uiColor.a = .3f;
    this.pickUpSound = pickUpSound;
  }

}
