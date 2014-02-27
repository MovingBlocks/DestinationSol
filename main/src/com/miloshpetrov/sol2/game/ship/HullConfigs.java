package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.SolMath;

import java.util.ArrayList;
import java.util.HashMap;

public class HullConfigs {
  private final HashMap<String,HullConfig> myConfigs;

  public HullConfigs(ShipBuilder shipBuilder, TexMan texMan) {
    myConfigs = new HashMap<String, HullConfig>();


    JsonReader r = new JsonReader();
    JsonValue parsed = r.parse(SolFiles.readOnly(Const.CONFIGS_DIR + "ships.json"));
    for (JsonValue sh : parsed) {
      String texName = sh.getString("texName");
      float size = sh.getFloat("size");
      int maxLife = sh.getInt("maxLife");
      Vector2 e1Pos = SolMath.readV2(sh, "e1Pos");
      Vector2 e2Pos = SolMath.readV2(sh, "e2Pos");
      Vector2 g1Pos = SolMath.readV2(sh, "g1Pos");
      Vector2 g2Pos = SolMath.readV2(sh, "g2Pos");
      ArrayList<Vector2> lightSrcPoss = SolMath.readV2List(sh, "lightSrcPoss");
      float durability = sh.getFloat("durability");
      boolean hasBase = sh.getBoolean("hasBase");
      ArrayList<Vector2> forceBeaconPoss = SolMath.readV2List(sh, "forceBeaconPoss");
      ArrayList<Vector2> doorPoss = SolMath.readV2List(sh, "doorPoss");
      HullConfig.Type type = HullConfig.Type.forValue(sh.getString("type"));
      HullConfig c = new HullConfig(texName, size, maxLife, e1Pos, e2Pos, g1Pos, g2Pos, lightSrcPoss, durability,
        hasBase, forceBeaconPoss, doorPoss, texMan, type);
      process(c, shipBuilder);
      myConfigs.put(sh.name, c);
    }


    myConfigs.put("corvette", getCorvette(shipBuilder, texMan));
    myConfigs.put("hawk", getHawk(shipBuilder, texMan));
    myConfigs.put("dragon", getDragon(shipBuilder, texMan));
    myConfigs.put("hummer", getHummer(shipBuilder, texMan));
    myConfigs.put("orbiter", getOrbiter(shipBuilder, texMan));
    myConfigs.put("hunter", getHunter(shipBuilder, texMan));
    myConfigs.put("vanguard", getVanguard(shipBuilder, texMan));
    myConfigs.put("guardie", getGuardie(shipBuilder, texMan));
    myConfigs.put("truck", getTruck(shipBuilder, texMan));
    myConfigs.put("drome", getDrome(shipBuilder, texMan));
    myConfigs.put("station", getStation(shipBuilder, texMan));
  }

  public HullConfig getConfig(String name) {
    return myConfigs.get(name);
  }

  private HullConfig getHawk(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.5f, .5f);
    Vector2 gun2Pos = new Vector2();
    Vector2 engine1Pos = new Vector2();
    Vector2 engine2Pos = new Vector2();

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    lights.add(new Vector2(.25f, .5f));
    lights.add(new Vector2(.75f, .5f));
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    ArrayList<Vector2> doors = new ArrayList<Vector2>();

    HullConfig cfg = new HullConfig("hawk", .8f, 8, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, 3f, false, beacons, doors, texMan, HullConfig.Type.STD);
    process(cfg, shipBuilder);
    return cfg;
  }

  private HullConfig getHummer(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.5f, .4f);
    Vector2 gun2Pos = new Vector2();
    Vector2 engine1Pos = new Vector2();
    Vector2 engine2Pos = new Vector2();

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    lights.add(new Vector2(.535f, .3f));
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    ArrayList<Vector2> doors = new ArrayList<Vector2>();

    HullConfig cfg = new HullConfig("hummer", .8f, 10, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, 3f, false, beacons, doors, texMan, HullConfig.Type.STD);
    process(cfg, shipBuilder);
    return cfg;
  }

  private HullConfig getCorvette(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.5f, .15f);
    Vector2 gun2Pos = new Vector2(.5f, .85f);

    Vector2 engine1Pos = new Vector2(0, .2f);
    Vector2 engine2Pos = new Vector2(0, .8f);

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    ArrayList<Vector2> doorPoss = new ArrayList<Vector2>();

    HullConfig cfg = new HullConfig("corvette", .5f, 13, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, .7f, true, beacons, doorPoss, texMan, HullConfig.Type.STD);
    process(cfg, shipBuilder);
    return cfg;
  }

  private HullConfig getGuardie(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.3f, .28f);
    Vector2 gun2Pos = new Vector2(.3f, .72f);
    Vector2 engine1Pos = new Vector2(.1f, .47f);
    Vector2 engine2Pos = new Vector2(.1f, .53f);

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    ArrayList<Vector2> doors = new ArrayList<Vector2>();

    HullConfig cfg = new HullConfig("guardie", .4f, 20, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, .7f, true, beacons, doors, texMan, HullConfig.Type.STD);
    process(cfg, shipBuilder);
    return cfg;
  }

  private HullConfig getOrbiter(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.25f, .28f);
    Vector2 gun2Pos = new Vector2(.3f, .7f);
    Vector2 engine1Pos = new Vector2(.1f, .25f);
    Vector2 engine2Pos = new Vector2(0, .65f);

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    ArrayList<Vector2> doors = new ArrayList<Vector2>();

    HullConfig cfg = new HullConfig("orbiter", .5f, 50, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, .7f, true, beacons, doors, texMan, HullConfig.Type.STD);
    process(cfg, shipBuilder);
    return cfg;
  }

  private HullConfig getHunter(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.22f, .32f);
    Vector2 gun2Pos = new Vector2(.22f, .68f);
    Vector2 engine1Pos = new Vector2(0, .3f);
    Vector2 engine2Pos = new Vector2(0, .7f);

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    ArrayList<Vector2> doorPoss = new ArrayList<Vector2>();

    HullConfig cfg = new HullConfig("hunter", 1f, 60, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, .7f, true, beacons, doorPoss, texMan, HullConfig.Type.STD);
    process(cfg, shipBuilder);
    return cfg;
  }

  private HullConfig getTruck(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.77f, .5f);
    Vector2 gun2Pos = new Vector2();
    Vector2 engine1Pos = new Vector2(.14f, .33f);
    Vector2 engine2Pos = new Vector2(.14f, .67f);

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    ArrayList<Vector2> doors = new ArrayList<Vector2>();

    HullConfig cfg = new HullConfig("truck", 2f, 60, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, 3f, true, beacons, doors, texMan, HullConfig.Type.BIG);
    process(cfg, shipBuilder);
    return cfg;
  }

  private HullConfig getVanguard(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.66f, .33f);
    Vector2 gun2Pos = new Vector2(.66f, .66f);
    Vector2 engine1Pos = new Vector2(.13f, .38f);
    Vector2 engine2Pos = new Vector2(.13f, .60f);

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    ArrayList<Vector2> doors = new ArrayList<Vector2>();

    HullConfig cfg = new HullConfig("vanguard", .9f, 80, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, .7f, true, beacons, doors, texMan, HullConfig.Type.STD);
    process(cfg, shipBuilder);
    return cfg;
  }

  private HullConfig getDragon(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.37f, .18f);
    Vector2 gun2Pos = new Vector2(.3f, .74f);
    Vector2 engine1Pos = new Vector2(0, .3f);
    Vector2 engine2Pos = new Vector2(0, .7f);

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    ArrayList<Vector2> doors = new ArrayList<Vector2>();

    HullConfig cfg = new HullConfig("dragon", 1.5f, 90, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, 3f, false, beacons, doors, texMan, HullConfig.Type.STD);
    process(cfg, shipBuilder);
    return cfg;
  }

  private HullConfig getDrome(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.24f, .09f);
    Vector2 gun2Pos = new Vector2();
    Vector2 engine1Pos = new Vector2();
    Vector2 engine2Pos = new Vector2();

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    lights.add(new Vector2(.43f, .23f));
    lights.add(new Vector2(.87f, .23f));
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    ArrayList<Vector2> doors = new ArrayList<Vector2>();

    HullConfig cfg = new HullConfig("drome", 2f, 100, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, 0, false, beacons, doors, texMan, HullConfig.Type.STATION);
    process(cfg, shipBuilder);
    return cfg;
  }

  private HullConfig getStation(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.82f, .82f);
    Vector2 gun2Pos = new Vector2(.5f, .07f);
    Vector2 engine1Pos = new Vector2();
    Vector2 engine2Pos = new Vector2();

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    lights.add(new Vector2(.26f, .93f));
    lights.add(new Vector2(.73f, .93f));
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    ArrayList<Vector2> doors = new ArrayList<Vector2>();

    HullConfig cfg = new HullConfig("station", 8f, 200, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, 0, false, beacons, doors, texMan, HullConfig.Type.STATION);
    process(cfg, shipBuilder);
    return cfg;
  }

  private void process(HullConfig config, ShipBuilder shipBuilder) {
    Vector2 o = shipBuilder.getOrigin(config.texName);
    config.g1Pos.sub(o).scl(config.size);
    config.g2Pos.sub(o).scl(config.size);
    config.e1Pos.sub(o).scl(config.size);
    config.e2Pos.sub(o).scl(config.size);
    for (Vector2 pos : config.lightSrcPoss) pos.sub(o).scl(config.size);
    for (Vector2 pos : config.forceBeaconPoss) pos.sub(o).scl(config.size);
    for (Vector2 pos : config.doorPoss) pos.sub(o).scl(config.size);
  }
}
