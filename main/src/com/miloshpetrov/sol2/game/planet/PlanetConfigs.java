package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.*;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.PathLoader;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.HullConfigs;

import java.util.*;

public class PlanetConfigs {
  private final Map<String, PlanetConfig> myConfigs;

  public PlanetConfigs(TexMan texMan, HullConfigs hullConfigs) {
    myConfigs = new HashMap<String, PlanetConfig>();

    JsonReader r = new JsonReader();
    JsonValue parsed = r.parse(SolFiles.readOnly("res/configs/planets.json"));
    for (JsonValue sh : parsed) {
      float minGrav = sh.getFloat("minGrav");
      float maxGrav = sh.getFloat("maxGrav");
      List<DecoConfig> deco = loadDecoConfigs(sh);
      ArrayList<PlanetEnemyConfig> groundEnemies = loadEnemiyConfigs(sh.get("groundEnemies"), hullConfigs);
      ArrayList<PlanetEnemyConfig> orbitEnemies = loadEnemiyConfigs(sh.get("orbitEnemies"), hullConfigs);
      HashMap<SurfDir, Map<SurfDir, List<Tile>>> groundTiles = new HashMap<SurfDir, Map<SurfDir, List<Tile>>>();
      fillTiles(texMan, groundTiles, sh.name);
      PlanetConfig c = new PlanetConfig(sh.name, minGrav, maxGrav, deco, groundEnemies, orbitEnemies, groundTiles);
      myConfigs.put(sh.name, c);
    }
  }

  private ArrayList<PlanetEnemyConfig> loadEnemiyConfigs(JsonValue enemies, HullConfigs hullConfigs) {
    ArrayList<PlanetEnemyConfig> res = new ArrayList<PlanetEnemyConfig>();
    for (JsonValue e : enemies) {
      String hullName = e.getString("hull");
      HullConfig hull = hullConfigs.getConfig(hullName);
      String items = e.getString("items");
      float density = e.getFloat("density");
      PlanetEnemyConfig c = new PlanetEnemyConfig(hull, items, density);
      res.add(c);
    }
    return res;
  }

  private List<DecoConfig> loadDecoConfigs(JsonValue planetConfig) {
    ArrayList<DecoConfig> res = new ArrayList<DecoConfig>();
    for (JsonValue deco : planetConfig.get("deco")) {
      float density = deco.getFloat("density");
      float szMin = deco.getFloat("szMin");
      float szMax = deco.getFloat("szMax");
      Vector2 orig = SolMath.readV2(deco, "orig");
      boolean allowFlip = deco.getBoolean("allowFlip");
      DecoConfig c = new DecoConfig(deco.name, density, szMin, szMax, orig, allowFlip);
      res.add(c);
    }
    return res;
  }

  public PlanetConfig getConfig(String name) {
    return myConfigs.get(name);
  }

  public PlanetConfig getRandom() {
    return SolMath.elemRnd(new ArrayList<PlanetConfig>(myConfigs.values()));
  }

  private static void fillTiles(TexMan texMan, Map<SurfDir, Map<SurfDir, List<Tile>>> groundTiles, String name) {
    PathLoader pathLoader = new PathLoader("grounds/" + name);
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
        Array<TextureAtlas.AtlasRegion> regs = texMan.getPack("grounds/" + name + "/" + tileDescName);
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
