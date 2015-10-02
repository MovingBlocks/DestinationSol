package org.destinationsol.game.item;

import com.badlogic.gdx.graphics.Color;
import org.destinationsol.game.sound.SolSound;

public class SolItemType {
  public final Color color;
  public final SolSound pickUpSound;
  public final Color uiColor;
  public final float sz;

  public SolItemType(Color color, SolSound pickUpSound, float sz) {
    this.color = color;
    this.sz = sz;
    uiColor = new Color(color);
    uiColor.a = .3f;
    this.pickUpSound = pickUpSound;
  }

}
