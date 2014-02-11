package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.game.PathLoader;

import java.util.*;

public class TileMan {

  public final Map<SurfDir, Map<SurfDir, List<Tile>>> groundTiles;

  public TileMan(TexMan texMan) {
    groundTiles = new HashMap<SurfDir, Map<SurfDir, List<Tile>>>();
    fillTiles(texMan);
  }

  private void fillTiles(TexMan texMan) {
    PathLoader pathLoader = new PathLoader(Gdx.files.internal("res/paths/grounds.json"));
    PathLoader.Model paths = pathLoader.getInternalModel();

    for (SurfDir from : SurfDir.values()) {
      HashMap<SurfDir, List<Tile>> fromMap = new HashMap<SurfDir, List<Tile>>();
      groundTiles.put(from, fromMap);
      for (SurfDir to : SurfDir.values()) {
        if (from == SurfDir.DOWN && to == SurfDir.DOWN) continue;
        ArrayList<Tile> tileVariants = new ArrayList<Tile>();
        fromMap.put(to, tileVariants);
        boolean inverted = from == SurfDir.DOWN || to == SurfDir.UP;
        String fromL = from.getLetter();
        String toL = to.getLetter();
        String tileDescName = inverted ? toL + fromL : fromL + toL;
        Array<TextureAtlas.AtlasRegion> regs = texMan.getPack("grounds/" + tileDescName);
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
          tileVariants.add(new Tile(reg, points, to, from));
          i++;
        }
      }
    }
  }
}
