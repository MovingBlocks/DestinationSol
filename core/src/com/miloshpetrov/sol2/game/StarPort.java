package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.*;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.particle.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.ArrayList;
import java.util.List;

public class StarPort implements SolObject {

  public static final float DIST_FROM_PLANET = Const.PLANET_GAP * .5f;
  public static final int SIZE = 8;
  public static final float FARE = 10f;
  private final Body myBody;
  private final ArrayList<LightSrc> myLights;
  private final Vector2 myPos;
  private final Planet myFrom;
  private final Planet myTo;
  private final ArrayList<Dra> myDras;
  private float myAngle;
  private final boolean mySecondary;

  public StarPort(Planet from, Planet to, Body body, ArrayList<Dra> dras, boolean secondary, ArrayList<LightSrc> lights) {
    myFrom = from;
    myTo = to;
    myDras = dras;
    myBody = body;
    myLights = lights;
    myPos = new Vector2();
    setParamsFromBody();
    mySecondary = secondary;
  }

  @Override
  public void update(SolGame game) {
    setParamsFromBody();

    float fps = 1 / game.getTimeStep();

    Vector2 spd = getDesiredPos(myFrom, myTo, true);
    spd.sub(myPos).scl(fps/4);
    myBody.setLinearVelocity(spd);
    SolMath.free(spd);
    float desiredAngle = SolMath.angle(myFrom.getPos(), myTo.getPos());
    myBody.setAngularVelocity((desiredAngle - myAngle) * SolMath.degRad * fps/4);

    SolShip ship = ForceBeacon.pullShips(game, this, myPos, null, null, .4f * SIZE);
    if (ship != null && ship.getMoney() >= FARE && ship.getPos().dst(myPos) < .05f * SIZE) {
      ship.setMoney(ship.getMoney() - FARE);
      Transcendent t = new Transcendent(ship, myFrom, myTo, game);
      ObjectManager objectManager = game.getObjMan();
      objectManager.addObjDelayed(t);
      blip(game, ship);
      game.getSoundMan().play(game, game.getSpecialSounds().transcendentCreated, null, t);
      objectManager.removeObjDelayed(ship);
    }
    for (int i = 0, myLightsSize = myLights.size(); i < myLightsSize; i++) {
      LightSrc l = myLights.get(i);
      l.update(true, myAngle, game);
    }

  }

  private static void blip(SolGame game, SolShip ship) {
    TextureAtlas.AtlasRegion tex = game.getTexMan().getTex(Teleport.TEX_PATH, null);
    float blipSz = ship.getHull().config.approxRadius * 10;
    game.getPartMan().blip(game, ship.getPos(), SolMath.rnd(180), blipSz, 1, Vector2.Zero, tex);
  }

  public boolean isSecondary() {
    return mySecondary;
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return false;
  }

  @Override
  public void onRemove(SolGame game) {
    myBody.getWorld().destroyBody(myBody);

  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
    game.getSpecialSounds().playHit(game, this, pos, dmgType);
  }

  @Override
  public boolean receivesGravity() {
    return false;
  }

  @Override
  public void receiveForce(Vector2 force, SolGame game, boolean acc) {

  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public FarObj toFarObj() {
    return new MyFar(myFrom, myTo, myPos, mySecondary);
  }

  @Override
  public List<Dra> getDras() {
    return myDras;
  }

  @Override
  public float getAngle() {
    return myAngle;
  }

  @Override
  public Vector2 getSpd() {
    return null;
  }

  @Override
  public void handleContact(SolObject other, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game, Vector2 collPos)
  {

  }

  @Override
  public String toDebugString() {
    return null;
  }

  @Override
  public Boolean isMetal() {
    return true;
  }

  @Override
  public boolean hasBody() {
    return true;
  }

  private void setParamsFromBody() {
    myPos.set(myBody.getPosition());
    myAngle = myBody.getAngle() * SolMath.radDeg;
  }

  @Bound
  public static Vector2 getDesiredPos(Planet from, Planet to, boolean percise) {
    Vector2 fromPos = from.getPos();
    float angle = SolMath.angle(fromPos, to.getPos(), percise);
    Vector2 pos = SolMath.getVec();
    SolMath.fromAl(pos, angle, from.getFullHeight() + DIST_FROM_PLANET);
    pos.add(fromPos);
    return pos;
  }

  public Planet getFrom() {
    return myFrom;
  }

  public Planet getTo() {
    return myTo;
  }

  public static class Builder {
    public static final float FLOW_DIST = .26f * SIZE;
    private final PathLoader myLoader;

    public Builder() {
      myLoader = new PathLoader("misc");
    }

    public StarPort build(SolGame game, Planet from, Planet to, boolean secondary) {
      float angle = SolMath.angle(from.getPos(), to.getPos());
      Vector2 pos = getDesiredPos(from, to, false);
      ArrayList<Dra> dras = new ArrayList<Dra>();
      Body body = myLoader.getBodyAndSprite(game, "smallGameObjs", "starPort", SIZE,
        BodyDef.BodyType.KinematicBody, new Vector2(pos), angle, dras, 10f, DraLevel.BIG_BODIES, null);
      SolMath.free(pos);
      ArrayList<LightSrc> lights = new ArrayList<LightSrc>();
      addFlow(game, pos, dras, 0, lights);
      addFlow(game, pos, dras, 90, lights);
      addFlow(game, pos, dras, -90, lights);
      addFlow(game, pos, dras, 180, lights);
      ParticleSrc force = game.getSpecialEffects().buildForceBeacon(FLOW_DIST * 1.5f, game, new Vector2(), pos, Vector2.Zero);
      force.setWorking(true);
      dras.add(force);
      StarPort sp = new StarPort(from, to, body, dras, secondary, lights);
      body.setUserData(sp);
      return sp;
    }

    private void addFlow(SolGame game, Vector2 pos, ArrayList<Dra> dras, float angle, ArrayList<LightSrc> lights) {
      EffectConfig flow = game.getSpecialEffects().starPortFlow;
      Vector2 relPos = new Vector2();
      SolMath.fromAl(relPos, angle, -FLOW_DIST);
      ParticleSrc f1 = new ParticleSrc(flow, FLOW_DIST, DraLevel.PART_BG_0, relPos, false, game, pos, Vector2.Zero, angle);
      f1.setWorking(true);
      dras.add(f1);
      LightSrc light = new LightSrc(game, .6f, true, 1, relPos, flow.tint);
      light.collectDras(dras);
      lights.add(light);
    }
  }

  public static class MyFar implements FarObj {
    private final Planet myFrom;
    private final Planet myTo;
    private final Vector2 myPos;
    private final boolean mySecondary;
    private float myAngle;

    public MyFar(Planet from, Planet to, Vector2 pos, boolean secondary) {
      myFrom = from;
      myTo = to;
      myPos = new Vector2(pos);
      mySecondary = secondary;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
      return false;
    }

    @Override
    public SolObject toObj(SolGame game) {
      return game.getStarPortBuilder().build(game, myFrom, myTo, mySecondary);
    }

    @Override
    public void update(SolGame game) {

      Vector2 dp = getDesiredPos(myFrom, myTo, false);
      myPos.set(dp);
      SolMath.free(dp);
      myAngle = SolMath.angle(myFrom.getPos(), myTo.getPos());
    }

    @Override
    public float getRadius() {
      return SIZE/2;
    }

    @Override
    public Vector2 getPos() {
      return myPos;
    }

    @Override
    public String toDebugString() {
      return null;
    }

    @Override
    public boolean hasBody() {
      return true;
    }

    public Planet getFrom() {
      return myFrom;
    }

    public Planet getTo() {
      return myTo;
    }

    public float getAngle() {
      return myAngle;
    }

    public boolean isSecondary() {
      return mySecondary;
    }
  }

  public static class Transcendent implements SolObject {
    private static final float TRAN_SZ = 1f;
    private final Planet myFrom;
    private final Planet myTo;
    private final Vector2 myPos;
    private final Vector2 myDestPos;
    private final ArrayList<Dra> myDras;
    private final FarShip myShip;
    private final Vector2 mySpd;
    private final LightSrc myLight;

    private float myAngle;
    private final ParticleSrc myEff;

    public Transcendent(SolShip ship, Planet from, Planet to, SolGame game) {
      myShip = ship.toFarObj();
      myFrom = from;
      myTo = to;
      myPos = new Vector2(ship.getPos());
      mySpd = new Vector2();
      myDestPos = new Vector2();

      RectSprite s = new RectSprite(game.getTexMan().getTex("smallGameObjs/transcendent", null), TRAN_SZ, .3f, 0, new Vector2(), DraLevel.PROJECTILES, 0, 0, SolColor.W, false);
      myDras = new ArrayList<Dra>();
      myDras.add(s);
      EffectConfig eff = game.getSpecialEffects().transcendentWork;
      myEff = new ParticleSrc(eff, TRAN_SZ, DraLevel.PART_BG_0, new Vector2(), true, game, myPos, Vector2.Zero, 0);
      myEff.setWorking(true);
      myDras.add(myEff);
      myLight = new LightSrc(game, .6f * TRAN_SZ, true, .5f, new Vector2(), eff.tint);
      myLight.collectDras(myDras);
      setDependentParams();
    }

    public FarShip getShip() {
      return myShip;
    }

    @Override
    public void update(SolGame game) {
      setDependentParams();

      float ts = game.getTimeStep();
      Vector2 moveDiff = SolMath.getVec(mySpd);
      moveDiff.scl(ts);
      myPos.add(moveDiff);
      SolMath.free(moveDiff);

      if (myPos.dst(myDestPos) < .5f) {
        ObjectManager objectManager = game.getObjMan();
        objectManager.removeObjDelayed(this);
        myShip.setPos(myPos);
        myShip.setSpd(new Vector2());
        SolShip ship = myShip.toObj(game);
        objectManager.addObjDelayed(ship);
        blip(game, ship);
        game.getSoundMan().play(game, game.getSpecialSounds().transcendentFinished, null, this);
        game.getObjMan().resetDelays(); // because of the hacked speed
      } else {
        game.getSoundMan().play(game, game.getSpecialSounds().transcendentMove, null, this);
        myLight.update(true, myAngle, game);
      }
    }

    private void setDependentParams() {
      Vector2 toPos = myTo.getPos();
      float nodeAngle = SolMath.angle(toPos, myFrom.getPos());
      SolMath.fromAl(myDestPos, nodeAngle, myTo.getFullHeight() + DIST_FROM_PLANET + SIZE/2);
      myDestPos.add(toPos);
      myAngle = SolMath.angle(myPos, myDestPos);
      SolMath.fromAl(mySpd, myAngle, Const.MAX_MOVE_SPD * 2); //hack again : (
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
      return false;
    }

    @Override
    public void onRemove(SolGame game) {
      game.getPartMan().finish(game, myEff, myPos);
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
      game.getSpecialSounds().playHit(game, this, pos, dmgType);
    }

    @Override
    public boolean receivesGravity() {
      return false;
    }

    @Override
    public void receiveForce(Vector2 force, SolGame game, boolean acc) {
    }

    @Override
    public Vector2 getPos() {
      return myPos;
    }

    @Override
    public FarObj toFarObj() {
      return null;
    }

    @Override
    public List<Dra> getDras() {
      return myDras;
    }

    @Override
    public float getAngle() {
      return myAngle;
    }

    @Override
    public Vector2 getSpd() {
      return mySpd;
    }

    @Override
    public void handleContact(SolObject other, ContactImpulse impulse, boolean isA, float absImpulse,
      SolGame game, Vector2 collPos)
    {
    }

    @Override
    public String toDebugString() {
      return null;
    }

    @Override
    public Boolean isMetal() {
      return null;
    }

    @Override
    public boolean hasBody() {
      return false;
    }
  }
}
