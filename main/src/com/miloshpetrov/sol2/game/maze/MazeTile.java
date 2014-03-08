package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class MazeTile {
  public final TextureAtlas.AtlasRegion tex;
  public final List<List<Vector2>> points;

  public MazeTile(TextureAtlas.AtlasRegion tex, List<List<Vector2>> points) {
    this.tex = tex;
    this.points = points;
  }
}
