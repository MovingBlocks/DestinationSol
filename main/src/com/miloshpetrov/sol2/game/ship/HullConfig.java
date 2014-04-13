package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.item.EngineItem;

import java.util.ArrayList;

public class HullConfig {
  public static final float MIN_IDLE_DIST = .8f;
  public final String texName;
  public final float size;
  public final int maxLife;
  public final Vector2 e1Pos;
  public final Vector2 e2Pos;
  public final Vector2 g1Pos;
  public final Vector2 g2Pos;
  public final ArrayList<Vector2> lightSrcPoss;
  public final float durability;
  public final boolean hasBase;
  public final ArrayList<Vector2> forceBeaconPoss;
  public final ArrayList<Vector2> doorPoss;
  public final TextureAtlas.AtlasRegion icon;
  public final Type type;
  public final TextureAtlas.AtlasRegion tex;
  public final EngineItem.Config engineConfig;
  public final AbilityConfig ability;
  public final float approxRadius;

  public HullConfig(String texName, float size, int maxLife, Vector2 e1Pos, Vector2 e2Pos, Vector2 g1Pos,
    Vector2 g2Pos, ArrayList<Vector2> lightSrcPoss,
    float durability, boolean hasBase, ArrayList<Vector2> forceBeaconPoss, ArrayList<Vector2> doorPoss,
    Type type, TextureAtlas.AtlasRegion icon, TextureAtlas.AtlasRegion tex, EngineItem.Config engineConfig,
    AbilityConfig ability)
  {
    this.texName = texName;
    this.size = size;
    this.approxRadius = .4f * size;
    this.maxLife = maxLife;
    this.e1Pos = e1Pos;
    this.e2Pos = e2Pos;
    this.g1Pos = g1Pos;
    this.g2Pos = g2Pos;
    this.lightSrcPoss = lightSrcPoss;
    this.forceBeaconPoss = forceBeaconPoss;
    this.durability = durability;
    this.hasBase = hasBase;
    this.doorPoss = doorPoss;
    this.icon = icon;
    this.type = type;
    this.tex = tex;
    this.engineConfig = engineConfig;
    this.ability = ability;
  }

  public float getMaxIdleDist() {
    float res = .4f * size;
    return res < MIN_IDLE_DIST ? MIN_IDLE_DIST : res;
  }

  public static enum Type {
    STD("std"), BIG("big"), STATION("station");

    private final String myName;

    Type(String name) {
      myName = name;
    }

    public static Type forName(String name) {
      for (Type t : Type.values()) {
        if (t.myName.equals(name)) return t;
      }
      return null;
    }
  }
}
