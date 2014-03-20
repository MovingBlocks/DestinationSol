package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.DebugAspects;
import com.miloshpetrov.sol2.game.PathLoader;
import com.miloshpetrov.sol2.ui.DebugCollector;

import java.util.ArrayList;
import java.util.List;

public class MazeTile {
  public final TextureAtlas.AtlasRegion tex;
  public final List<List<Vector2>> points;

  public MazeTile(TextureAtlas.AtlasRegion tex, List<List<Vector2>> points) {
    this.tex = tex;
    this.points = points;
  }

  public static MazeTile load(TextureAtlas.AtlasRegion tex, PathLoader.Model paths, boolean wall, String pathEntryName) {
    ArrayList<List<Vector2>> points = new ArrayList<List<Vector2>>();
    PathLoader.RigidBodyModel tilePaths = paths.rigidBodies.get(pathEntryName);
    List<PathLoader.PolygonModel> shapes = tilePaths == null ? new ArrayList<PathLoader.PolygonModel>() : tilePaths.shapes;
    for (PathLoader.PolygonModel shape : shapes) {
      List<Vector2> vertices = new ArrayList<Vector2>(shape.vertices);
      points.add(vertices);
    }
    if (points.isEmpty() && wall) {
      if (DebugAspects.PHYSICS_DEBUG) DebugCollector.warn("found no paths for " + pathEntryName);
      ArrayList<Vector2> wallPoints = new ArrayList<Vector2>();
      wallPoints.add(new Vector2(0, .4f));
      wallPoints.add(new Vector2(1, .45f));
      wallPoints.add(new Vector2(1, .55f));
      wallPoints.add(new Vector2(0, .6f));
      points.add(wallPoints);
    }
    return new MazeTile(tex, points);
  }
}
