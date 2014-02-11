package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.TexMan;

import java.util.ArrayList;

public class HullConfigs {
  public static final float DUR_HARD = 3f;
  public static final float DUR_SOFT = .7f;
  public final HullConfig corvette;
  public final HullConfig hawk;
  public final HullConfig dragon;
  public final HullConfig bus;
  public final HullConfig hummer;
  public final HullConfig orbiter;
  public final HullConfig hunter;
  public final HullConfig vanguard;
  public final HullConfig guardie;
  public final HullConfig truck;
  public final HullConfig drome;
  public final HullConfig station;

  public HullConfigs(ShipBuilder shipBuilder, TexMan texMan) {
    corvette = getCorvette(shipBuilder, texMan);
    hawk = getHawk(shipBuilder, texMan);
    dragon = getDragon(shipBuilder, texMan);
    bus = getBus(shipBuilder, texMan);
    hummer = getHummer(shipBuilder, texMan);
    orbiter = getOrbiter(shipBuilder, texMan);
    hunter = getHunter(shipBuilder, texMan);
    vanguard = getVanguard(shipBuilder, texMan);
    guardie = getGuardie(shipBuilder, texMan);
    truck = getTruck(shipBuilder, texMan);
    drome = getDrome(shipBuilder, texMan);
    station = getStation(shipBuilder, texMan);
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

    HullConfig cfg = new HullConfig("hawk", .8f, 8, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, DUR_HARD, false, beacons, doors, texMan, HullConfig.Type.STD);
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

    HullConfig cfg = new HullConfig("hummer", .8f, 10, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, DUR_HARD, false, beacons, doors, texMan, HullConfig.Type.STD);
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

    HullConfig cfg = new HullConfig("corvette", .5f, 13, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, DUR_SOFT, true, beacons, doorPoss, texMan, HullConfig.Type.STD);
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

    HullConfig cfg = new HullConfig("guardie", .4f, 20, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, DUR_SOFT, true, beacons, doors, texMan, HullConfig.Type.STD);
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

    HullConfig cfg = new HullConfig("orbiter", .5f, 50, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, DUR_SOFT, true, beacons, doors, texMan, HullConfig.Type.STD);
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

    HullConfig cfg = new HullConfig("hunter", 1f, 60, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, DUR_SOFT, true, beacons, doorPoss, texMan, HullConfig.Type.STD);
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

    HullConfig cfg = new HullConfig("truck", 2f, 60, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, DUR_HARD, true, beacons, doors, texMan, HullConfig.Type.BIG);
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

    HullConfig cfg = new HullConfig("vanguard", .9f, 80, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, DUR_SOFT, true, beacons, doors, texMan, HullConfig.Type.STD);
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

    HullConfig cfg = new HullConfig("dragon", 1.5f, 90, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, DUR_HARD, false, beacons, doors, texMan, HullConfig.Type.STD);
    process(cfg, shipBuilder);
    return cfg;
  }

  private HullConfig getBus(ShipBuilder shipBuilder, TexMan texMan) {
    Vector2 gun1Pos = new Vector2(.75f, .5f);
    Vector2 gun2Pos = new Vector2(.28f, .5f);
    Vector2 engine1Pos = new Vector2(.09f, .28f);
    Vector2 engine2Pos = new Vector2(.09f, .72f);

    ArrayList<Vector2> lights = new ArrayList<Vector2>();
    ArrayList<Vector2> beacons = new ArrayList<Vector2>();
    beacons.add(new Vector2(.50f, .25f));
    beacons.add(new Vector2(.50f, .75f));
    ArrayList<Vector2> doors = new ArrayList<Vector2>();
    doors.add(new Vector2(.50f, .13f));
    doors.add(new Vector2(.50f, .87f));

    HullConfig cfg = new HullConfig("bus", 3.5f, 100, engine1Pos, engine2Pos, gun1Pos, gun2Pos, lights, DUR_HARD, false, beacons, doors, texMan, HullConfig.Type.BIG);
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
    Vector2 o = shipBuilder.getOrigin(config.name);
    config.g1RelPos.sub(o).scl(config.size);
    config.g2RelPos.sub(o).scl(config.size);
    config.e1RelPos.sub(o).scl(config.size);
    config.e2RelPos.sub(o).scl(config.size);
    for (Vector2 pos : config.lightSrcRelPoss) pos.sub(o).scl(config.size);
    for (Vector2 pos : config.forceBeaconPoss) pos.sub(o).scl(config.size);
    for (Vector2 pos : config.doorPoss) pos.sub(o).scl(config.size);
  }
}
