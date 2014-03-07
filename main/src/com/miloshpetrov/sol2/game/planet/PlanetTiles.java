package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.PathLoader;

import java.util.*;

public class PlanetTiles {

  private final Map<SurfDir,Map<SurfDir,List<Tile>>> myGroundTiles;

  public PlanetTiles(TexMan texMan, String configName) {
    myGroundTiles = new HashMap<SurfDir, Map<SurfDir, List<Tile>>>();
    loadGround(texMan, configName);
//    loadEntrances(texMan, configName);
  }

  private void loadEntrances(TexMan texMan, String configName) {
    String folder = "dungeons/" + configName;
    PathLoader pathLoader = new PathLoader(folder);
    PathLoader.Model paths = pathLoader.getInternalModel();
  }

  private void loadGround(TexMan texMan, String configName) {
    String folder = "grounds/" + configName;
    PathLoader pathLoader = new PathLoader(folder);
    PathLoader.Model paths = pathLoader.getInternalModel();

    for (SurfDir from : SurfDir.values()) {
      HashMap<SurfDir, List<Tile>> fromMap = new HashMap<SurfDir, List<Tile>>();
      myGroundTiles.put(from, fromMap);
      for (SurfDir to : SurfDir.values()) {
        if (from == SurfDir.DOWN && to == SurfDir.DOWN) continue;
        boolean inverted = from == SurfDir.DOWN || to == SurfDir.UP;
        String fromL = from.getLetter();
        String toL = to.getLetter();
        String tileDescName = inverted ? toL + fromL : fromL + toL;
        ArrayList<Tile> tileVariants = buildTiles(texMan, paths, inverted, tileDescName, folder, from, to);
        fromMap.put(to, tileVariants);
      }
    }
  }

  private ArrayList<Tile> buildTiles(TexMan texMan, PathLoader.Model paths, boolean inverted, String tileDescName,
    String folder, SurfDir from, SurfDir to)
  {
    Array<TextureAtlas.AtlasRegion> regs = texMan.getPack(folder + "/" + tileDescName);
    ArrayList<Tile> tileVariants = new ArrayList<Tile>();
    int i = 0;
    for (TextureAtlas.AtlasRegion reg : regs) {
      if (inverted) {
        reg = texMan.getFlipped(reg);
      }
      String tileName = tileDescName + "_" + i + ".png";
      List<Vector2> points = new ArrayList<Vector2>();
      PathLoader.RigidBodyModel tilePaths = paths.rigidBodies.get(tileName);
      List<PathLoader.PolygonModel> shapes = tilePaths == null ? null : tilePaths.shapes;
      if (shapes != null && !shapes.isEmpty()) {
        PathLoader.PolygonModel shape = shapes.get(0);
        List<Vector2> vertices = shape.vertices;
        int sz = vertices.size();
        for (int j = 0; j < sz; j++) {
          Vector2 v = vertices.get(inverted ? sz - j - 1 : j);
          Vector2 point = new Vector2(v.x - .5f, v.y - .5f);
          if (inverted) point.x *= -1;
          points.add(point);
        }
      }
      tileVariants.add(new Tile(reg, points, from, to));
      i++;
    }
    return tileVariants;
  }

  public Tile getGround(SurfDir from, SurfDir to) {
    List<Tile> list = myGroundTiles.get(from).get(to);
    return SolMath.elemRnd(list);
  }

  public Tile getDungeonEntrance(boolean down, boolean left, boolean right) {
    return null;
  }
}
