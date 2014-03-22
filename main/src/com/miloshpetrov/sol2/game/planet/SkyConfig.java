package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.utils.JsonValue;

public class SkyConfig {
  public final float[] dawnHsba;
  public final float[] dayHsba;

  public SkyConfig(float[] dawnHsba, float[] dayHsba) {
    this.dawnHsba = dawnHsba;
    this.dayHsba = dayHsba;
  }

  public static SkyConfig load(JsonValue skyNode) {
    float[] dawnHsba = loadHsba(skyNode.get("dawnColor"));
    float[] dayHsba = loadHsba(skyNode.get("dayColor"));
    return new SkyConfig(dawnHsba, dayHsba);
  }

  private static float[] loadHsba(JsonValue node) {
    float h = node.getFloat("h");
    float s = node.getFloat("s");
    float b = node.getFloat("b");
    float a = node.getFloat("a");
    return new float[] {h, s, b, a};
  }
}
