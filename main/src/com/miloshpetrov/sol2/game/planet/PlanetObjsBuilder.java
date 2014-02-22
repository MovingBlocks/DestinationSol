package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.input.*;
import com.miloshpetrov.sol2.game.ship.HullConfig;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.*;

public class PlanetObjsBuilder {
  private static final int ROWS = 6;
  private static final float PURE_GROUND_PERC = .333f;
  private static final float TOP_TILE_SZ = 2f;

  private static final float MAX_CLOUD_PIECE_SZ = 1.5f;
  private static final float MAX_CLOUT_PIECE_ROT_SPD = 5f;
  private static final int MAX_CLOUD_PIECE_COUNT = 30;
  private static final float MAX_CLOUD_PIECE_DIST_SHIFT = 1f;
  private static final float MAX_CLOUD_LINEAR_SPD = .1f;
  private static final float AVG_CLOUD_LINEAR_WIDTH = 3f;
  private static final float CLOUD_DENSITY = .2f;

  private static final float DECO_PACK_SZ = 5f;
  private static final float DECO_PACK_ANGULAR_WIDTH = 360 * DECO_PACK_SZ / (2 * SolMath.PI * Const.MAX_GROUND_HEIGHT);


  public float createPlanetObjs(SolGame game, Planet planet) {
    if (DebugAspects.NO_OBJS) return 0;
    float minR = createGround(game, planet);
    createClouds(game, planet);
    createDeco(game, planet);
    Sky sky = new Sky(game, planet);
    game.getObjMan().addObjDelayed(sky);
    createEnemies(game, planet);
    return minR;
  }

  private void createEnemies(SolGame game, Planet planet) {
    ArrayList<Float> takenAngles = new ArrayList<Float>();
    SolShip b = buildGroundBase(game, planet, takenAngles);
    game.getObjMan().addObjNow(game, b);
    float gh = planet.getGroundHeight();

    PlanetConfig config = planet.getConfig();
    for (PlanetEnemyConfig ge : config.groundEnemies) {
      int count = (int) (ge.density * gh);
      for (int i = 0; i < count; i++) {
        SolShip e = buildGroundEnemy(game, planet, takenAngles, ge);
        game.getObjMan().addObjDelayed(e);
      }

    }

    for (PlanetEnemyConfig oe : config.orbitEnemies) {
      int count = (int) (oe.density * gh * Const.ATM_HEIGHT);
      for (int i = 0; i < count; i++) {
        float heightPerc = .6f * i / count + .2f;
        SolShip e = buildOrbitEnemy(game, planet, heightPerc, oe);
        game.getObjMan().addObjDelayed(e);
      }
    }
  }

  private float createGround(SolGame game, Planet planet) {
    // helper values
    float maxR = planet.getGroundHeight() - TOP_TILE_SZ / 2;
    int cols = (int)(2 * SolMath.PI * maxR / TOP_TILE_SZ);
    if (cols <= 0) throw new RuntimeException("eh");


    // helper arrays
    float[] radii = new float[ROWS];
    float[] tileSizes = new float[ROWS];
    float currRadius = maxR;
    for (int row = 0; row < ROWS; row++) {
      float tileSize = 2 * SolMath.PI * currRadius / cols;
      radii[row] = currRadius;
      tileSizes[row] = tileSize;
      currRadius -= tileSize;
    }
    float minR = radii[ROWS - 1] - tileSizes[ROWS - 1] / 2;

    // fill slots
    Tile[][] slots = new Tile[cols][ROWS];
    fillSlots(planet.getConfig(), slots, cols);


    // create ground
    for (int row = 0; row < ROWS; row++) {
      float tileDist = radii[row];
      float tileSize = tileSizes[row];
      for (int col = 0; col < cols; col++) {
        Tile tile = slots[col][row];
        if (tile == null) continue;
        float toPlanetRelAngle = 360f * col / cols;
        TileObj to = new TileObjBuilder().build(game, tileSize, toPlanetRelAngle, tileDist, tile, planet);
        game.getObjMan().addObjNow(game, to);
      }
    }

    return minR;
  }

  private void fillSlots(PlanetConfig planetConfig, Tile[][] slots, int cols) {
    float[] ds0 = new float[cols];
    float desiredMin = 0;
    float desiredMax = (1 - PURE_GROUND_PERC) * ROWS;

    for (int x = 0; x < cols; x++) {
      ds0[x] = SolMath.rnd(desiredMin, desiredMax);
    }
    float[] ds = new float[cols];
    float min = Float.MAX_VALUE;
    float max = Float.MIN_VALUE;
    for (int x = 0; x < cols; x++) {
      float prev = x == 0 ? ds0[cols - 1] : ds0[x - 1];
      float next = x == cols - 1 ? ds0[0] : ds0[x + 1];
      ds[x] = .5f * .5f * (prev + next) + .5f * ds0[x];
      if (ds[x] < min) min = ds[x];
      if (max < ds[x]) max = ds[x];
    }
    float shift = min - desiredMin;
    float mul = (desiredMax - desiredMin) / (max - min);
    for (int x = 0; x < cols; x++) {
      ds[x] = mul * (ds[x] - shift);
    }

    int nextD = (int) ds[0];
    for (int col = 0; col < cols; col++) {
      int prevD = nextD;
      nextD = col == cols - 1 ? (int) ds[0] : (int) ds[col];
      for (int row = 0; row < ROWS; row++) {
        SurfDir from = SurfDir.FWD;
        SurfDir to = SurfDir.FWD;
        if (row < prevD) {
          from = SurfDir.DOWN;
        } else if (row > prevD) {
          from = SurfDir.UP;
        }
        if (row < nextD) {
          to = SurfDir.DOWN;
        } else if (row > nextD) {
          to = SurfDir.UP;
        }
        if (from == SurfDir.DOWN && to == SurfDir.DOWN) continue;
        slots[col][row] = planetConfig.groundTiles.get(from).get(to).get(SolMath.test(.5f) ? 0 : 1);
      }
    }
  }

  private void createClouds(SolGame game, Planet planet) {
    int cloudCount = SolMath.intRnd(.7f, (int) (CLOUD_DENSITY * Const.ATM_HEIGHT * planet.getGroundHeight()));
    for (int i = 0; i < cloudCount; i++) {
      PlanetSprites cloud = createCloud(game.getTexMan(), planet);
      game.getObjMan().addObjDelayed(cloud);
    }
  }

  private PlanetSprites createCloud(TexMan texMan, Planet planet) {
    float distPerc = SolMath.rnd(0, 1);
    float dist = planet.getGroundHeight() - TOP_TILE_SZ + .9f * Const.ATM_HEIGHT * distPerc;
    float angle = SolMath.rnd(180);

    ArrayList<Dra> dras = new ArrayList<Dra>();
    float sizePerc = SolMath.rnd(.2f, 1);
    float linearWidth = sizePerc * (distPerc + .5f) * AVG_CLOUD_LINEAR_WIDTH;
    float maxAngleShift = SolMath.arcSin(linearWidth / 2 / dist) * 2;
    float maxDistShift = (1 - distPerc) * MAX_CLOUD_PIECE_DIST_SHIFT;

    int pieceCount = (int) (sizePerc * MAX_CLOUD_PIECE_COUNT);
    for (int i = 0; i < pieceCount; i++) {
      RectSprite s = createCloudSprite(texMan, maxAngleShift, maxDistShift, dist);
      dras.add(s);
    }
    float rotSpd = SolMath.rnd(.1f, 1) * SolMath.arcSin(MAX_CLOUD_LINEAR_SPD / dist);

    return new PlanetSprites(planet, angle, dist, dras, rotSpd);
  }

  private RectSprite createCloudSprite(TexMan texMan, float maxAngleShift, float maxDistShift,
    float baseDist) {

    TextureAtlas.AtlasRegion tex = texMan.getRndTex("skies/cloud", null);
    float angleShiftRel = SolMath.rnd(1);
    float distPerc = 1 - SolMath.abs(angleShiftRel);
    float sz = .5f * (1 + distPerc) * MAX_CLOUD_PIECE_SZ;

    float relAngle = SolMath.rnd(30);
    float rotSpd = SolMath.rnd(MAX_CLOUT_PIECE_ROT_SPD);
    float angleShift = angleShiftRel * maxAngleShift;
    float distShift = maxDistShift == 0 ? 0 : distPerc * SolMath.rnd(0, maxDistShift);
    float dist = baseDist + distShift;
    Vector2 basePos = SolMath.getVec(0, -baseDist);
    Vector2 relPos = new Vector2(0, -dist);
    SolMath.rotate(relPos, angleShift, true);
    relPos.sub(basePos);
    SolMath.free(basePos);

    return new RectSprite(tex, sz, 0, 0, relPos, DraLevel.CLOUDS, relAngle, rotSpd, Col.W);
  }

  public void createDeco(SolGame game, Planet planet) {
    float groundHeight = planet.getGroundHeight();
    Vector2 planetPos = planet.getPos();
    float planetAngle = planet.getAngle();
    Map<Vector2, List<Dra>> collector = new HashMap<Vector2, List<Dra>>();
    PlanetConfig config = planet.getConfig();
    for (DecoConfig dc : config.deco) {
      addDeco0(game, groundHeight, planetPos, "deco/" + config.name + "/" + dc.texName, dc.density, dc.szMin, dc.szMax,
        dc.orig.x, dc.orig.y, dc.allowFlip, collector);
    }

    for (Map.Entry<Vector2, List<Dra>> e : collector.entrySet()) {
      Vector2 packPos = e.getKey();
      List<Dra> ss = e.getValue();
      float packAngle = SolMath.angle(planetPos, packPos, true) - planetAngle;
      float packDist = packPos.dst(planetPos);
      PlanetSprites ps = new PlanetSprites(planet, packAngle, packDist, ss, 0);
      game.getObjMan().addObjDelayed(ps);
    }
  }

  private void addDeco0(SolGame game, float groundHeight, Vector2 planetPos, String texName,
    float decoDensity, float szMin, float szMax, float origX, float origY, boolean allowFlip,
    Map<Vector2, List<Dra>> collector)
  {
    World w = game.getObjMan().getWorld();

    final Vector2 rayCasted = new Vector2();
    RayCastCallback rcc = new RayCastCallback() {
      @Override
      public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if (!(fixture.getBody().getUserData() instanceof TileObj)) {
          return -1;
        }
        rayCasted.set(point);
        return fraction;
      }
    };

    int decoCount = (int) (2 * SolMath.PI * groundHeight * decoDensity);
    for (int i = 0; i < decoCount; i++) {
      float decoAngle = SolMath.rnd(180);
      SolMath.fromAl(rayCasted, decoAngle, groundHeight, true);
      rayCasted.add(planetPos);
      w.rayCast(rcc, rayCasted, planetPos);
      float decoDist = rayCasted.dst(planetPos);

      float baseAngle = SolMath.windowCenter(decoAngle, DECO_PACK_ANGULAR_WIDTH);
      float baseDist = SolMath.windowCenter(decoDist, DECO_PACK_SZ);
      Vector2 basePos = SolMath.fromAl(baseAngle, baseDist).add(planetPos);
      Vector2 decoRelPos = new Vector2(rayCasted).sub(basePos);
      SolMath.rotate(decoRelPos, -baseAngle - 90, true);
      float decoRelAngle = decoAngle - baseAngle;

      float decoSz = SolMath.rnd(szMin, szMax);
      TextureAtlas.AtlasRegion decoTex = game.getTexMan().getRndTex(texName, allowFlip ? null : false);
      RectSprite s = new RectSprite(decoTex, decoSz, origX, origY, decoRelPos, DraLevel.DECO, decoRelAngle, 0, Col.W);
      List<Dra> ss = collector.get(basePos);
      if (ss == null) {
        ss = new ArrayList<Dra>();
        collector.put(new Vector2(basePos), ss);
      }
      ss.add(s);
      SolMath.free(basePos);
    }
  }

  private SolShip buildGroundBase(SolGame game, Planet planet, ArrayList<Float> takenAngles) {
    HullConfig config = game.getHullConfigs().getConfig("drome");
    return buildGroundShip(game, planet, config, "bo", "", Fraction.LAANI, takenAngles);
  }

  private SolShip buildGroundEnemy(SolGame game, Planet planet, ArrayList<Float> takenAngles, PlanetEnemyConfig ge) {
        return buildGroundShip(game, planet, ge.hull, ge.items, null, Fraction.EHAR, takenAngles);
  }

  public SolShip buildGroundShip(SolGame game, Planet planet, HullConfig hullConfig, String ic, String tc,
    Fraction fraction, ArrayList<Float> takenAngles)
  {
    Vector2 pos = game.getPlanetMan().findLandingPlace(game, planet, takenAngles);
    float aboveGround = hullConfig.size * (hullConfig.type == HullConfig.Type.STATION ? .25f : .5f);
    float height = pos.len();
    pos.scl((height + aboveGround)/height);
    SolMath.toWorld(pos, pos, planet.getAngle(), planet.getPos());

    Vector2 toPlanet = SolMath.getVec(planet.getPos()).sub(pos);
    float angle = toPlanet.angle() - 90;
    Vector2 spd = new Vector2(toPlanet).nor();
    SolMath.free(toPlanet);

    float detectionDist = game.getCam().getGroundViewDist();
    Pilot provider = new AiPilot(new NoDestProvider(), false, fraction, true, null, detectionDist);

    return game.getShipBuilder().buildNew(game, pos, spd, angle, 0, provider, ic, hullConfig, false, false,
      null, false, 30f, tc);
  }

  public SolShip buildOrbitEnemy(SolGame game, Planet planet, float heightPerc, PlanetEnemyConfig oe) {
    float height = planet.getGroundHeight() + heightPerc * Const.ATM_HEIGHT;
    Vector2 pos = new Vector2();
    SolMath.fromAl(pos, SolMath.rnd(180), height);
    Vector2 planetPos = planet.getPos();
    pos.add(planetPos);
    float spdLen = SolMath.sqrt(planet.getGravConst() / height);
    boolean cw = SolMath.test(.5f);
    if (!cw) spdLen *= -1;
    Vector2 spd = new Vector2(0, -spdLen);
    Vector2 v = SolMath.distVec(pos, planetPos);
    SolMath.rotate(spd, v.angle());
    SolMath.free(v);
    float detectionDist = game.getCam().getSpaceViewDist();

    OrbiterDestProvider dp = new OrbiterDestProvider(planet, height, cw);
    Pilot provider = new AiPilot(dp, false, Fraction.EHAR, true, null, detectionDist);

    return game.getShipBuilder().buildNew(game, pos, spd, 0, 0, provider, oe.items, oe.hull, true, false,
      null, true, 40f, null);
   }
}
