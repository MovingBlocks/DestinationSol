package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.gun.GunItem;
import com.miloshpetrov.sol2.game.gun.GunMount;
import com.miloshpetrov.sol2.game.input.Pilot;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.particle.LightSrc;

import java.util.ArrayList;
import java.util.List;

public class ShipBuilder {
  public static final float SHIP_DENSITY = 3f;

  private final PathLoader myPathLoader;

  public ShipBuilder() {
    myPathLoader = new PathLoader("hulls");
  }

  public SolShip buildNew(SolGame game, Vector2 pos, Vector2 spd, float angle, float rotSpd, Pilot pilot,
    String items, HullConfig hullConfig,
    RemoveController removeController,
    boolean hasRepairer, float money, TradeConfig tradeConfig)
  {
    ItemMan itemMan = game.getItemMan();
    if (spd == null) spd = new Vector2();
    ItemContainer ic = new ItemContainer();
    itemMan.fillContainer(ic, items);

    ShipRepairer repairer = hasRepairer ? new ShipRepairer() : null;
    TradeContainer tc = tradeConfig == null ? null : new TradeContainer(tradeConfig);
    EngineItem.Config ec = hullConfig.engineConfig;
    EngineItem ei = ec == null ? null : ec.example.copy();
    SolShip ship = build(game, pos, spd, angle, rotSpd, pilot, ic, hullConfig, hullConfig.maxLife,
      null, null, removeController, ei, repairer, money, tc, null, null);
    boolean g1eq = false;
    for (SolItem item : ic) {
      boolean isGun = item instanceof GunItem;
      if (g1eq && isGun) {
        ship.maybeEquip(game, item, true, true);
        continue;
      }
      boolean ok = ship.maybeEquip(game, item, false, true);
      if (ok && isGun) g1eq = true;
    }
    return ship;
  }

  public SolShip build(SolGame game, Vector2 pos, Vector2 spd, float angle, float rotSpd, Pilot pilot,
    ItemContainer container, HullConfig hullConfig, float life,
    GunItem gun1, GunItem gun2, RemoveController removeController,
    EngineItem engine, ShipRepairer repairer, float money, TradeContainer tradeContainer, Shield shield, Armor armor)
  {
    ArrayList<Dra> dras = new ArrayList<Dra>();
    ShipHull hull = buildHull(game, pos, spd, angle, rotSpd, hullConfig, life, dras);
    SolShip ship = new SolShip(game, pilot, hull, removeController, dras, container, repairer, money, tradeContainer, shield, armor);
    hull.getBody().setUserData(ship);
    for (Door door : hull.getDoors()) door.getBody().setUserData(ship);

    if (engine != null) {
      hull.setEngine(game, ship, engine);
    }
    if (gun1 != null) {
      hull.getGunMount(false).setGun(game, ship, gun1, hullConfig.g1UnderShip);
    }
    if (gun2 != null) {
      hull.getGunMount(true).setGun(game, ship, gun2, hullConfig.g1UnderShip);
    }
    return ship;
  }

  private ShipHull buildHull(SolGame game, Vector2 pos, Vector2 spd, float angle, float rotSpd, HullConfig hullConfig,
    float life, ArrayList<Dra> dras)
  {
    BodyDef.BodyType bodyType = hullConfig.type == HullConfig.Type.STATION ? BodyDef.BodyType.KinematicBody : BodyDef.BodyType.DynamicBody;
    DraLevel level = hullConfig.type == HullConfig.Type.STD ? DraLevel.BODIES : DraLevel.BIG_BODIES;
    Body body = myPathLoader.getBodyAndSprite(game, "hulls", hullConfig.texName, hullConfig.size, bodyType, pos, angle,
      dras, SHIP_DENSITY, level, hullConfig.tex);
    Fixture shieldFixture = createShieldFixture(hullConfig, body);

    GunMount m1 = new GunMount(hullConfig.g1Pos, hullConfig.mount1CanFix);
    GunMount m2 = hullConfig.g2Pos == null ? null : new GunMount(hullConfig.g2Pos, hullConfig.mount2CanFix);

    List<LightSrc> lCs = new ArrayList<LightSrc>();
    for (Vector2 p : hullConfig.lightSrcPoss) {
      LightSrc lc = new LightSrc(game, .35f, true, .7f, p, game.getCols().hullLights);
      lc.collectDras(dras);
      lCs.add(lc);
    }

    ArrayList<ForceBeacon> beacons = new ArrayList<ForceBeacon>();
    for (Vector2 relPos : hullConfig.forceBeaconPoss) {
      ForceBeacon fb = new ForceBeacon(game, relPos, pos, spd);
      fb.collectDras(dras);
      beacons.add(fb);
    }

    ArrayList<Door> doors = new ArrayList<Door>();
    for (Vector2 doorRelPos : hullConfig.doorPoss) {
      Door door = createDoor(game, pos, angle, body, doorRelPos);
      door.collectDras(dras);
      doors.add(door);
    }

    Fixture base = getBase(hullConfig.hasBase, body);
    ShipHull hull = new ShipHull(game, hullConfig, body, m1, m2, base, lCs, life, beacons, doors, shieldFixture);
    body.setLinearVelocity(spd);
    body.setAngularVelocity(rotSpd * SolMath.degRad);
    return hull;
  }

  private Fixture createShieldFixture(HullConfig hullConfig, Body body) {
    CircleShape shieldShape = new CircleShape();
    shieldShape.setRadius(Shield.SIZE_PERC * hullConfig.size);
    FixtureDef shieldDef = new FixtureDef();
    shieldDef.shape = shieldShape;
    shieldDef.isSensor = true;
    Fixture shieldFixture = body.createFixture(shieldDef);
    shieldShape.dispose();
    return shieldFixture;
  }

  private Door createDoor(SolGame game, Vector2 pos, float angle, Body body, Vector2 doorRelPos) {
    World w = game.getObjMan().getWorld();
    TextureAtlas.AtlasRegion tex = game.getTexMan().getTex("misc/door", null);
    PrismaticJoint joint = createDoorJoint(body, w, pos, doorRelPos, angle);
    RectSprite s = new RectSprite(tex, Door.DOOR_LEN, 0, 0, new Vector2(doorRelPos), DraLevel.BODIES, 0, 0, Col.W, false);
    return new Door(joint, s);
  }


  private PrismaticJoint createDoorJoint(Body shipBody, World w, Vector2 shipPos, Vector2 doorRelPos, float shipAngle) {
    Body doorBody = createDoorBody(w, shipPos, doorRelPos, shipAngle);
    PrismaticJointDef jd = new PrismaticJointDef();
    jd.initialize(shipBody, doorBody, shipPos, Vector2.Zero);
    jd.localAxisA.set(1, 0);
    jd.collideConnected = false;
    jd.enableLimit = true;
    jd.enableMotor = true;
    jd.lowerTranslation = 0;
    jd.upperTranslation = Door.DOOR_LEN;
    jd.maxMotorForce = 2;
    return (PrismaticJoint) w.createJoint(jd);
  }

  private Body createDoorBody(World world, Vector2 shipPos, Vector2 doorRelPos, float shipAngle) {
    BodyDef bd = new BodyDef();
    bd.type = BodyDef.BodyType.DynamicBody;
    bd.angle = shipAngle * SolMath.degRad;
    bd.angularDamping = 0;
    bd.linearDamping = 0;
    SolMath.toWorld(bd.position, doorRelPos, shipAngle, shipPos, false);
    Body body = world.createBody(bd);
    PolygonShape shape = new PolygonShape();
    shape.setAsBox(Door.DOOR_LEN/2, .03f);
    body.createFixture(shape, SHIP_DENSITY);
    shape.dispose();
    return body;
  }

  private static Fixture getBase(boolean hasBase, Body body) {
    if (!hasBase) return null;
    Fixture base = null;
    Vector2 v = SolMath.getVec();
    float lowestX = Float.MAX_VALUE;
    for (Fixture f : body.getFixtureList()) {
      Shape s = f.getShape();
      if (!(s instanceof PolygonShape)) continue;
      PolygonShape poly = (PolygonShape) s;
      int pointCount = poly.getVertexCount();
      for (int i = 0; i < pointCount; i++) {
        poly.getVertex(i, v);
        if (v.x < lowestX) {
          base = f;
          lowestX = v.x;
        }
      }
    }
    SolMath.free(v);
    return base;
  }

  public Vector2 getOrigin(String name) {
    return myPathLoader.getOrigin(name + ".png", 1);
  }
}
