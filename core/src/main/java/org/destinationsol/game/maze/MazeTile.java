package org.destinationsol.game.maze;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.PathLoader;
import org.destinationsol.game.asteroid.AsteroidBuilder;

import java.util.ArrayList;
import java.util.List;

public class MazeTile {
  public final TextureAtlas.AtlasRegion tex;
  public final List<List<Vector2>> points;
  public final boolean metal;
  public final TextureAtlas.AtlasRegion bgTex;

  public MazeTile(TextureAtlas.AtlasRegion tex, List<List<Vector2>> points, boolean metal,
    TextureAtlas.AtlasRegion bgTex)
  {
    this.tex = tex;
    this.points = points;
    this.metal = metal;
    this.bgTex = bgTex;
  }

  public static MazeTile load(TextureAtlas.AtlasRegion tex, PathLoader.Model paths, boolean wall, String pathEntryName,
    boolean metal, TextureAtlas.AtlasRegion bgTex)
  {
    ArrayList<List<Vector2>> points = new ArrayList<List<Vector2>>();
    PathLoader.RigidBodyModel tilePaths = paths.rigidBodies.get(AsteroidBuilder.removePath(pathEntryName));
    List<PathLoader.PolygonModel> shapes = tilePaths == null ? new ArrayList<PathLoader.PolygonModel>() : tilePaths.shapes;
    for (PathLoader.PolygonModel shape : shapes) {
      List<Vector2> vertices = new ArrayList<Vector2>(shape.vertices);
      points.add(vertices);
    }
    if (points.isEmpty() && wall) {
      DebugOptions.MISSING_PHYSICS_ACTION.handle("found no paths for " + pathEntryName);
      ArrayList<Vector2> wallPoints = new ArrayList<Vector2>();
      wallPoints.add(new Vector2(0, .4f));
      wallPoints.add(new Vector2(1, .45f));
      wallPoints.add(new Vector2(1, .55f));
      wallPoints.add(new Vector2(0, .6f));
      points.add(wallPoints);
    }
    return new MazeTile(tex, points, metal, bgTex);
  }
}
