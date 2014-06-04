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
import com.miloshpetrov.sol2.game.item.TradeConfig;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.*;

public class PlanetObjsBuilder {
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
    if (DebugOptions.NO_OBJS) return 0;
    float minR = createGround(game, planet);
//    createClouds(game, planet);
//    createDeco(game, planet);
    if (planet.getConfig().skyConfig != null) {
      Sky sky = new Sky(game, planet);
      game.getObjMan().addObjDelayed(sky);
    }
//    createEnemies(game, planet);
    return minR;
  }

  private void createEnemies(SolGame game, Planet planet) {
    ConsumedAngles takenAngles = new ConsumedAngles();

    ShipConfig cfg = planet.getConfig().stationConfig;
    if (cfg != null) {
      FarShip b = buildGroundShip(game, planet, cfg, planet.getConfig().tradeConfig, Fraction.LAANI, takenAngles);
      game.getObjMan().addFarObjNow(b);
    }

    float gh = planet.getGroundHeight();

    PlanetConfig config = planet.getConfig();
    for (ShipConfig ge : config.groundEnemies) {
      int count = (int) (ge.density * gh);
      for (int i = 0; i < count; i++) {
        FarShip e = buildGroundShip(game, planet, ge, null, Fraction.EHAR, takenAngles);
        game.getObjMan().addFarObjNow(e);
      }
    }

    buildOrbitEnemies(game, planet, gh, 0, .1f, config.lowOrbitEnemies, Const.AUTO_SHOOT_SPACE);
    buildOrbitEnemies(game, planet, gh, .1f, .6f, config.highOrbitEnemies, Const.AI_DET_DIST);
  }

  private void buildOrbitEnemies(SolGame game, Planet planet, float gh, float offsetPerc, float atmPerc,
    List<ShipConfig> configs, float detDist)
  {
    if (configs.isEmpty()) return;
    HashMap<ShipConfig, Integer> counts = new HashMap<ShipConfig, Integer>();
    int totalCount = 0;
    for (ShipConfig oe : configs) {
      int count = (int) (atmPerc * oe.density * gh * Const.ATM_HEIGHT);
      counts.put(oe, count);
      totalCount += count;
    }
    float stepPerc = atmPerc / totalCount;
    float heightPerc = offsetPerc;
    for (ShipConfig oe : configs) {
      int count = counts.get(oe);
      for (int i = 0; i < count; i++) {
        FarShip e = buildOrbitEnemy(game, planet, heightPerc, oe, detDist);
        game.getObjMan().addFarObjNow(e);
        heightPerc += stepPerc;
      }
    }
  }

  private float createGround(SolGame game, Planet planet) {
    // helper values
    float maxR = planet.getGroundHeight() - TOP_TILE_SZ / 2;
    int cols = (int)(2 * SolMath.PI * maxR / TOP_TILE_SZ);
    if (cols <= 0) throw new AssertionError("eh");
    int rows = planet.getConfig().rowCount;

    // helper arrays
    float[] radii = new float[rows];
    float[] tileSizes = new float[rows];
    float currRadius = maxR;
    for (int row = 0; row < rows; row++) {
      float tileSize = 2 * SolMath.PI * currRadius / cols;
      radii[row] = currRadius;
      tileSizes[row] = tileSize;
      currRadius -= tileSize;
    }
    float minR = radii[rows - 1] - tileSizes[rows - 1] / 2;

    Tile[][] tileMap = new GroundBuilder(planet.getConfig(), cols, rows).build();

    // create ground
    for (int row = 0; row < rows; row++) {
      float tileDist = radii[row];
      float tileSize = tileSizes[row];
      for (int col = 0; col < cols; col++) {
        Tile tile = tileMap[col][row];
        if (tile == null) continue;
        float toPlanetRelAngle = 360f * col / cols;
        if (tile.points.isEmpty()) {
          FarTileObj fto = new FarTileObj(planet, toPlanetRelAngle, tileDist, tileSize, tile);
          game.getObjMan().addFarObjNow(fto);
        } else {
          TileObj to = new TileObjBuilder().build(game, tileSize, toPlanetRelAngle, tileDist, tile, planet);
          game.getObjMan().addObjNow(game, to);
        }
      }
    }

    return minR;
  }

  private void createClouds(SolGame game, Planet planet) {
    ArrayList<TextureAtlas.AtlasRegion> cloudTexs = planet.getConfig().cloudTexs;
    if (cloudTexs.isEmpty()) return;
    int cloudCount = SolMath.intRnd(.7f, (int) (CLOUD_DENSITY * Const.ATM_HEIGHT * planet.getGroundHeight()));
    for (int i = 0; i < cloudCount; i++) {
      FarPlanetSprites cloud = createCloud(planet, cloudTexs, game.getTexMan());
      game.getObjMan().addFarObjNow(cloud);
    }
  }

  private FarPlanetSprites createCloud(Planet planet, ArrayList<TextureAtlas.AtlasRegion> cloudTexs, TexMan texMan) {
    float distPerc = SolMath.rnd(0, 1);
    float dist = planet.getGroundHeight() - TOP_TILE_SZ + .9f * Const.ATM_HEIGHT * distPerc;
    float angle = SolMath.rnd(180);

    ArrayList<Dra> dras = new ArrayList<Dra>();
    float sizePerc = SolMath.rnd(.2f, 1);
    float linearWidth = sizePerc * (distPerc + .5f) * AVG_CLOUD_LINEAR_WIDTH;
    float maxAngleShift = SolMath.arcToAngle(linearWidth, dist);
    float maxDistShift = (1 - distPerc) * MAX_CLOUD_PIECE_DIST_SHIFT;

    int pieceCount = (int) (sizePerc * MAX_CLOUD_PIECE_COUNT);
    for (int i = 0; i < pieceCount; i++) {
      RectSprite s = createCloudSprite(cloudTexs, maxAngleShift, maxDistShift, dist, texMan);
      dras.add(s);
    }
    float rotSpd = SolMath.rnd(.1f, 1) * SolMath.arcToAngle(MAX_CLOUD_LINEAR_SPD, dist);

    return new FarPlanetSprites(planet, angle, dist, dras, rotSpd);
  }

  private RectSprite createCloudSprite(ArrayList<TextureAtlas.AtlasRegion> cloudTexs,
    float maxAngleShift,
    float maxDistShift, float baseDist, TexMan texMan)
  {

    TextureAtlas.AtlasRegion tex = SolMath.elemRnd(cloudTexs);
    if (SolMath.test(.5f)) tex = texMan.getFlipped(tex);
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

    return new RectSprite(tex, sz, 0, 0, relPos, DraLevel.CLOUDS, relAngle, rotSpd, Col.W, false);
  }

  public void createDeco(SolGame game, Planet planet) {
    float groundHeight = planet.getGroundHeight();
    Vector2 planetPos = planet.getPos();
    float planetAngle = planet.getAngle();
    Map<Vector2, List<Dra>> collector = new HashMap<Vector2, List<Dra>>();
    PlanetConfig config = planet.getConfig();
    for (DecoConfig dc : config.deco) {
      addDeco0(game, groundHeight, planetPos, collector, dc);
    }

    for (Map.Entry<Vector2, List<Dra>> e : collector.entrySet()) {
      Vector2 packPos = e.getKey();
      List<Dra> ss = e.getValue();
      float packAngle = SolMath.angle(planetPos, packPos, true) - planetAngle;
      float packDist = packPos.dst(planetPos);
      FarPlanetSprites ps = new FarPlanetSprites(planet, packAngle, packDist, ss, 0);
      game.getObjMan().addFarObjNow(ps);
    }
  }

  private void addDeco0(SolGame game, float groundHeight, Vector2 planetPos,
    Map<Vector2, List<Dra>> collector, DecoConfig dc)
  {
    World w = game.getObjMan().getWorld();
    ConsumedAngles consumed = new ConsumedAngles();

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

    int decoCount = (int) (2 * SolMath.PI * groundHeight * dc.density);
    for (int i = 0; i < decoCount; i++) {
      float decoSz = SolMath.rnd(dc.szMin, dc.szMax);
      float angularHalfWidth = SolMath.angularWidthOfSphere(decoSz / 2, groundHeight);

      float decoAngle = 0;
      for (int j = 0; j < 5; j++) {
        decoAngle = SolMath.rnd(180);
        if (!consumed.isConsumed(decoAngle, angularHalfWidth)) {
          consumed.add(decoAngle, angularHalfWidth);
          break;
        }
      }

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


      TextureAtlas.AtlasRegion decoTex = SolMath.elemRnd(dc.texs);
      if (dc.allowFlip && SolMath.test(.5f)) decoTex = game.getTexMan().getFlipped(decoTex);

      RectSprite s = new RectSprite(decoTex, decoSz, dc.orig.x, dc.orig.y, decoRelPos, DraLevel.DECO, decoRelAngle, 0, Col.W, false);
      List<Dra> ss = collector.get(basePos);
      if (ss == null) {
        ss = new ArrayList<Dra>();
        collector.put(new Vector2(basePos), ss);
      }
      ss.add(s);
      SolMath.free(basePos);
    }
  }

  public FarShip buildGroundShip(SolGame game, Planet planet, ShipConfig ge,
    TradeConfig tc,
    Fraction fraction, ConsumedAngles takenAngles)
  {
    Vector2 pos = game.getPlanetMan().findFlatPlace(game, planet, takenAngles, ge.hull.approxRadius);
    boolean station = ge.hull.type == HullConfig.Type.STATION;
    String ic = ge.items;
    boolean hasRepairer;
    hasRepairer = fraction == Fraction.LAANI;
    int money = ge.money;
    float height = pos.len();
    float aboveGround;
    if (station) {
      aboveGround = ge.hull.size * .75f - ge.hull.origin.y;
    } else {
      aboveGround = ge.hull.approxRadius;
    }
    pos.scl((height + aboveGround)/height);
    SolMath.toWorld(pos, pos, planet.getAngle(), planet.getPos(), false);

    Vector2 toPlanet = SolMath.getVec(planet.getPos()).sub(pos);
    float angle = SolMath.angle(toPlanet) - 180;
    if (station) angle += 90;
    Vector2 spd = new Vector2(toPlanet).nor();
    SolMath.free(toPlanet);

    Pilot provider = new AiPilot(new StillGuard(pos, game, ge), false, fraction, true, null, Const.AI_DET_DIST);

    return game.getShipBuilder().buildNewFar(game, pos, spd, angle, 0, provider, ic, ge.hull,
      null, hasRepairer, money, tc);
  }

  public FarShip buildOrbitEnemy(SolGame game, Planet planet, float heightPerc, ShipConfig oe, float detDist) {
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
    SolMath.rotate(spd, SolMath.angle(v));
    SolMath.free(v);

    OrbiterDestProvider dp = new OrbiterDestProvider(planet, height, cw);
    Pilot provider = new AiPilot(dp, false, Fraction.EHAR, true, null, detDist);

    int money = oe.money;

    return game.getShipBuilder().buildNewFar(game, pos, spd, 0, 0, provider, oe.items, oe.hull,
      null, false, money, null);
  }

}
