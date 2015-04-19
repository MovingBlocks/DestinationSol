package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.TextureManager;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.DebugOptions;
import com.miloshpetrov.sol2.game.PathLoader;

import java.util.*;

public class PlanetTiles {

  private final Map<SurfaceDirection,Map<SurfaceDirection,List<Tile>>> myGroundTiles;

  public PlanetTiles(TextureManager textureManager, String groundFolder, FileHandle configFile) {
    myGroundTiles = new HashMap<SurfaceDirection, Map<SurfaceDirection, List<Tile>>>();
    loadGround(textureManager, groundFolder, configFile);
  }

  private void loadGround(TextureManager textureManager, String groundFolder, FileHandle configFile) {
    PathLoader pathLoader = new PathLoader(groundFolder);
    PathLoader.Model paths = pathLoader.getInternalModel();

    for (SurfaceDirection from : SurfaceDirection.values()) {
      HashMap<SurfaceDirection, List<Tile>> fromMap = new HashMap<SurfaceDirection, List<Tile>>();
      myGroundTiles.put(from, fromMap);
      for (SurfaceDirection to : SurfaceDirection.values()) {
        if (from == SurfaceDirection.DOWN && to == SurfaceDirection.DOWN) continue;
        boolean inverted = from == SurfaceDirection.DOWN || to == SurfaceDirection.UP;
        String fromL = from.getLetter();
        String toL = to.getLetter();
        String tileDescName = inverted ? toL + fromL : fromL + toL;
        ArrayList<TextureAtlas.AtlasRegion> texs = textureManager.getPack(groundFolder + "/" + tileDescName, configFile);
        ArrayList<Tile> tileVariants = buildTiles(textureManager, paths, inverted, tileDescName, from, to, texs);
        fromMap.put(to, tileVariants);
      }
    }
  }

  private ArrayList<Tile> buildTiles(TextureManager textureManager, PathLoader.Model paths, boolean inverted, String tileDescName,
    SurfaceDirection from, SurfaceDirection to, ArrayList<TextureAtlas.AtlasRegion> texs)
  {
    ArrayList<Tile> tileVariants = new ArrayList<Tile>();
    for (TextureAtlas.AtlasRegion tex : texs) {
      if (inverted) {
        tex = textureManager.getFlipped(tex);
      }
      String tileName = tileDescName + "_" + tex.index + ".png";
      List<Vector2> points = new ArrayList<Vector2>();
      List<Vector2> rawPoints;
      PathLoader.RigidBodyModel tilePaths = paths == null ? null : paths.rigidBodies.get(tileName);
      List<PathLoader.PolygonModel> shapes = tilePaths == null ? null : tilePaths.shapes;
      if (shapes != null && !shapes.isEmpty()) {
        rawPoints = shapes.get(0).vertices;
      } else {
        rawPoints = getDefaultRawPoints(inverted ? to : from, inverted ? from : to, tileName);
      }
      int sz = rawPoints.size();
      for (int j = 0; j < sz; j++) {
        Vector2 v = rawPoints.get(inverted ? sz - j - 1 : j);
        Vector2 point = new Vector2(v.x - .5f, v.y - .5f);
        if (inverted) point.x *= -1;
        points.add(point);
      }
      tileVariants.add(new Tile(tex, points, from, to));
    }
    return tileVariants;
  }

  private List<Vector2> getDefaultRawPoints(SurfaceDirection from, SurfaceDirection to, String tileName) {
    ArrayList<Vector2> res = new ArrayList<Vector2>();
    if (from == SurfaceDirection.UP && to == SurfaceDirection.UP) return res;
    DebugOptions.MISSING_PHYSICS_ACTION.handle("no path found for " + tileName);
    res.add(new Vector2(.25f, .75f));
    if (from == SurfaceDirection.FWD) {
      res.add(new Vector2(.25f, .5f));
    } else {
      res.add(new Vector2(.25f, .25f));
      res.add(new Vector2(.5f, .25f));
    }
    res.add(new Vector2(.5f, .5f));
    if (to == SurfaceDirection.FWD) {
      res.add(new Vector2(.75f, .5f));
      res.add(new Vector2(.75f, .75f));
    } else {
      res.add(new Vector2(.5f, .75f));
    }
    return res;
  }

  public Tile getGround(SurfaceDirection from, SurfaceDirection to) {
    List<Tile> list = myGroundTiles.get(from).get(to);
    return SolMath.elemRnd(list);
  }

  public Tile getDungeonEntrance(boolean down, boolean left, boolean right) {
    return null;
  }
}
