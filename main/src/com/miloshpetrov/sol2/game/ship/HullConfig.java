package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.TexMan;

import java.util.ArrayList;

public class HullConfig {
  public static final float MIN_IDLE_DIST = .8f;
  public final String name;
  public final float size;
  public final int maxLife;
  public final Vector2 e1RelPos;
  public final Vector2 e2RelPos;
  public final Vector2 g1RelPos;
  public final Vector2 g2RelPos;
  public final ArrayList<Vector2> lightSrcRelPoss;
  public final float durability;
  public final boolean hasBase;
  public final ArrayList<Vector2> forceBeaconPoss;
  public final ArrayList<Vector2> doorPoss;
  public final TextureAtlas.AtlasRegion icon;
  public final Type type;

  public HullConfig(String name, float size, int maxLife, Vector2 e1RelPos, Vector2 e2RelPos, Vector2 g1RelPos,
    Vector2 g2RelPos, ArrayList<Vector2> lightSrcRelPoss,
    float durability, boolean hasBase, ArrayList<Vector2> forceBeaconPoss, ArrayList<Vector2> doorPoss, TexMan texMan,
    Type type)
  {
    this.name = name;
    this.size = size;
    this.maxLife = maxLife;
    this.e1RelPos = e1RelPos;
    this.e2RelPos = e2RelPos;
    this.g1RelPos = g1RelPos;
    this.g2RelPos = g2RelPos;
    this.lightSrcRelPoss = lightSrcRelPoss;
    this.forceBeaconPoss = forceBeaconPoss;
    this.durability = durability;
    this.hasBase = hasBase;
    this.doorPoss = doorPoss;
    this.icon = texMan.getTex(TexMan.ICONS_DIR + this.name);
    this.type = type;
  }

  public float getMaxIdleDist() {
    float res = .4f * size;
    return res < MIN_IDLE_DIST ? MIN_IDLE_DIST : res;
  }

  public static enum Type {
    STD, BIG, STATION
  }
}
