package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.SolMath;

import java.util.ArrayList;
import java.util.List;

public class MazeTile {
  public final TextureAtlas.AtlasRegion tex;
  public final List<List<Vector2>> points;

  public MazeTile(TextureAtlas.AtlasRegion tex, List<List<Vector2>> points) {
    this.tex = tex;
    this.points = points;
  }

  public static MazeTile load(TexMan texMan, boolean wall, boolean inner) {
    String texName = inner ? "inner" : "border";
    texName += wall ? "Wall" : "Pass";
    texName += "_1";
    TextureAtlas.AtlasRegion tex = texMan.getTex(texName, SolMath.test(.5f), null);
    ArrayList<List<Vector2>> points = new ArrayList<List<Vector2>>();
    if (wall) {
      ArrayList<Vector2> wallPoints = new ArrayList<Vector2>();
      wallPoints.add(new Vector2(0, .45f));
      wallPoints.add(new Vector2(1, .45f));
      wallPoints.add(new Vector2(1, .55f));
      wallPoints.add(new Vector2(0, .55f));
      points.add(wallPoints);
    }
    return new MazeTile(tex, points);
  }
}
