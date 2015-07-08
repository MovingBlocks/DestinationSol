package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.common.SolColor;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.files.FileManager;
import com.miloshpetrov.sol2.files.HullConfigManager;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.gun.*;
import com.miloshpetrov.sol2.game.input.Pilot;
import com.miloshpetrov.sol2.game.item.*;
import com.miloshpetrov.sol2.game.particle.LightSrc;
import com.miloshpetrov.sol2.game.ship.hulls.HullConfig;
import com.miloshpetrov.sol2.game.ship.hulls.Hull;

import java.util.ArrayList;
import java.util.List;

public class ShipBuilder {
  public static final float SHIP_DENSITY = 3f;
  public static final float AVG_BATTLE_TIME = 30f;
  public static final float AVG_ALLY_LIFE_TIME = 75f;

  private final PathLoader myPathLoader;

  public ShipBuilder() {
    myPathLoader = new PathLoader();
  }

  public FarShip buildNewFar(SolGame game, Vector2 pos, Vector2 spd, float angle, float rotSpd, Pilot pilot,
    String items, HullConfig hullConfig,
    RemoveController removeController,
    boolean hasRepairer, float money, TradeConfig tradeConfig, boolean giveAmmo)
  {

    if (spd == null) spd = new Vector2();
    ItemContainer ic = new ItemContainer();
    game.getItemMan().fillContainer(ic, items);
    EngineItem.Config ec = hullConfig.getEngineConfig();
    EngineItem ei = ec == null ? null : ec.example.copy();
    TradeContainer tc = tradeConfig == null ? null : new TradeContainer(tradeConfig);


    GunItem g1 = null;
    GunItem g2 = null;
    Shield shield = null;
    Armor armor = null;
    for (List<SolItem> group : ic) {
      for (SolItem i : group) {
        if (i instanceof Shield) {
          shield = (Shield) i;
          continue;
        }
        if (i instanceof Armor) {
          armor = (Armor) i;
          continue;
        }
        if (i instanceof GunItem) {
          GunItem g = (GunItem) i;
          if (g1 == null && hullConfig.getGunSlot(0).allowsRotation() != g.config.fixed) {
            g1 = g;
            continue;
          }
          if (hullConfig.getNrOfGunSlots() > 1 && g2 == null && hullConfig.getGunSlot(1).allowsRotation() != g.config.fixed) g2 = g;
          continue;
        }
      }
    }

    if (giveAmmo) {
      addAbilityCharges(ic, hullConfig, pilot);
      addAmmo(ic, g1, pilot);
      addAmmo(ic, g2, pilot);
    }

    return new FarShip(new Vector2(pos), new Vector2(spd), angle, rotSpd, pilot, ic, hullConfig, hullConfig.getMaxLife(),
      g1, g2, removeController, ei, hasRepairer ? new ShipRepairer() : null, money, tc, shield, armor);
  }

  private void addAmmo(ItemContainer ic, GunItem g, Pilot pilot) {
    if (g == null) return;
    GunConfig gc = g.config;
    ClipConfig cc = gc.clipConf;
    if (cc.infinite) return;
    float clipUseTime = cc.size * gc.timeBetweenShots + gc.reloadTime;
    float lifeTime = pilot.getFraction() == Fraction.LAANI ? AVG_ALLY_LIFE_TIME : AVG_BATTLE_TIME;
    int count = 1 + (int) (lifeTime / clipUseTime) + SolMath.intRnd(0, 2);
    for (int i = 0; i < count; i++) {
      if (ic.canAdd(cc.example)) ic.add(cc.example.copy());
    }
  }

  private void addAbilityCharges(ItemContainer ic, HullConfig hc, Pilot pilot) {
    if (hc.getAbility() != null) {
      SolItem ex = hc.getAbility().getChargeExample();
      if (ex != null) {
        int count;
        if (pilot.isPlayer()) {
          count = 3;
        } else {
          float lifeTime = pilot.getFraction() == Fraction.LAANI ? AVG_ALLY_LIFE_TIME : AVG_BATTLE_TIME;
          count = (int) (lifeTime / hc.getAbility().getRechargeTime() * SolMath.rnd(.3f, 1));
        }
        for (int i = 0; i < count; i++) ic.add(ex.copy());
      }
    }
  }

  public SolShip build(SolGame game, Vector2 pos, Vector2 spd, float angle, float rotSpd, Pilot pilot,
    ItemContainer container, HullConfig hullConfig, float life,
    GunItem gun1, GunItem gun2, RemoveController removeController,
    EngineItem engine, ShipRepairer repairer, float money, TradeContainer tradeContainer, Shield shield, Armor armor)
  {
    ArrayList<Dra> dras = new ArrayList<Dra>();
    Hull hull = buildHull(game, pos, spd, angle, rotSpd, hullConfig, life, dras);
    SolShip ship = new SolShip(game, pilot, hull, removeController, dras, container, repairer, money, tradeContainer, shield, armor);
    hull.getBody().setUserData(ship);
    for (Door door : hull.getDoors()) door.getBody().setUserData(ship);

    if (engine != null) {
      hull.setEngine(game, ship, engine);
    }
    if (gun1 != null) {
      GunMount gunMount0 = hull.getGunMount(false);
      if (gunMount0.isFixed() == gun1.config.fixed) {
          gunMount0.setGun(game, ship, gun1, hullConfig.getGunSlot(0).isUnderneathHull());
      }
    }
    if (gun2 != null) {
      GunMount m2 = hull.getGunMount(true);
      if (m2 != null) {
        if (m2.isFixed() == gun2.config.fixed) m2.setGun(game, ship, gun2, hullConfig.g2IsUnderShip());
      }
    }
    return ship;
  }

  private Hull buildHull(SolGame game, Vector2 pos, Vector2 spd, float angle, float rotSpd, HullConfig hullConfig,
    float life, ArrayList<Dra> dras)
  {
      //TODO: This logic belongs in the HullConfigManager/HullConfig
      FileHandle hullPropertiesFile =  FileManager.getInstance().getHullsDirectory().child(hullConfig.getInternalName()).child(HullConfigManager.PROPERTIES_FILE_NAME);
      JsonReader reader = new JsonReader();
      JsonValue rigidBodyNode = reader.parse(hullPropertiesFile).get("rigidBody");
      myPathLoader.readJson(rigidBodyNode, hullConfig);

    BodyDef.BodyType bodyType = hullConfig.getType() == HullConfig.Type.STATION ? BodyDef.BodyType.KinematicBody : BodyDef.BodyType.DynamicBody;
    DraLevel level = hullConfig.getType() == HullConfig.Type.STD ? DraLevel.BODIES : DraLevel.BIG_BODIES;
    Body body = myPathLoader.getBodyAndSprite(game, hullConfig, hullConfig.getSize(), bodyType, pos, angle,
      dras, SHIP_DENSITY, level, hullConfig.getTexture());
    Fixture shieldFixture = createShieldFixture(hullConfig, body);


    GunMount gunMount0 = new GunMount(hullConfig.getGunSlot(0));
    GunMount gunMount1 = (hullConfig.getNrOfGunSlots() > 1)
            ? new GunMount(hullConfig.getGunSlot(1))
            : null;

    List<LightSrc> lCs = new ArrayList<LightSrc>();
    for (Vector2 p : hullConfig.getLightSourcePositions()) {
      LightSrc lc = new LightSrc(game, .35f, true, .7f, p, game.getCols().hullLights);
      lc.collectDras(dras);
      lCs.add(lc);
    }

    ArrayList<ForceBeacon> beacons = new ArrayList<ForceBeacon>();
    for (Vector2 relPos : hullConfig.getForceBeaconPositions()) {
      ForceBeacon fb = new ForceBeacon(game, relPos, pos, spd);
      fb.collectDras(dras);
      beacons.add(fb);
    }

    ArrayList<Door> doors = new ArrayList<Door>();
    for (Vector2 doorRelPos : hullConfig.getDoorPositions()) {
      Door door = createDoor(game, pos, angle, body, doorRelPos);
      door.collectDras(dras);
      doors.add(door);
    }

    Fixture base = getBase(hullConfig.hasBase(), body);
    Hull hull = new Hull(game, hullConfig, body, gunMount0, gunMount1, base, lCs, life, beacons, doors, shieldFixture);
    body.setLinearVelocity(spd);
    body.setAngularVelocity(rotSpd * SolMath.degRad);
    return hull;
  }

  private Fixture createShieldFixture(HullConfig hullConfig, Body body) {
    CircleShape shieldShape = new CircleShape();
    shieldShape.setRadius(Shield.SIZE_PERC * hullConfig.getSize());
    FixtureDef shieldDef = new FixtureDef();
    shieldDef.shape = shieldShape;
    shieldDef.isSensor = true;
    Fixture shieldFixture = body.createFixture(shieldDef);
    shieldShape.dispose();
    return shieldFixture;
  }

  private Door createDoor(SolGame game, Vector2 pos, float angle, Body body, Vector2 doorRelPos) {
    World w = game.getObjMan().getWorld();
    TextureAtlas.AtlasRegion tex = game.getTexMan().getTex("smallGameObjs/door", null);
    PrismaticJoint joint = createDoorJoint(body, w, pos, doorRelPos, angle);
    RectSprite s = new RectSprite(tex, Door.DOOR_LEN, 0, 0, new Vector2(doorRelPos), DraLevel.BODIES, 0, 0, SolColor.W, false);
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
