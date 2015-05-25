package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.game.GameCols;

public class SkyConfig {
  public final Color dawn;
  public final Color day;

  public SkyConfig(Color dawnHsba, Color dayHsba) {
    this.dawn = dawnHsba;
    this.day = dayHsba;
  }

  public static SkyConfig load(JsonValue skyNode, GameCols cols) {
    if (skyNode == null) return null;
    Color dawn = cols.load(skyNode.getString("dawnColor"));
    Color day = cols.load(skyNode.getString("dayColor"));
    return new SkyConfig(dawn, day);
  }

}
