package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.*;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.planet.Planet;
import com.miloshpetrov.sol2.game.ship.*;

import java.util.ArrayList;
import java.util.List;

public class StarPort implements SolObj {

  public static final float DIST_FROM_PLANET = Const.PLANET_GAP * .5f;
  public static final int SIZE = 8;
  public static final float FARE = 10f;
  private final Body myBody;
  private final Vector2 myPos;
  private final Planet myFrom;
  private final Planet myTo;
  private final ArrayList<Dra> myDras;
  private final float myRadius;
  private float myAngle;
  private final boolean mySecondary;

  public StarPort(Planet from, Planet to, Body body, ArrayList<Dra> dras, boolean secondary) {
    myFrom = from;
    myTo = to;
    myDras = dras;
    myBody = body;
    myPos = new Vector2();
    myRadius = DraMan.radiusFromDras(myDras);
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

    SolShip ship = ForceBeacon.pullShips(game, null, myPos, null, null, .4f * SIZE);
    if (ship != null && ship.getMoney() >= FARE && ship.getPos().dst(myPos) < .05f * SIZE) {
      ship.setMoney(ship.getMoney() - FARE);
      Transcendent t = new Transcendent(game.getTexMan(), ship, myFrom, myTo);
      ObjMan objMan = game.getObjMan();
      objMan.addObjDelayed(t);
      objMan.removeObjDelayed(ship);
    }
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
  public float getRadius() {
    return myRadius;
  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
    game.getSpecialSounds().playDmg(game, this, pos, dmgType);
  }

  @Override
  public boolean receivesGravity() {
    return false;
  }

  @Override
  public void receiveAcc(Vector2 acc, SolGame game) {

  }

  @Override
  public Vector2 getPos() {
    return myPos;
  }

  @Override
  public FarObj toFarObj() {
    return new MyFar(myFrom, myTo, myPos, myRadius, mySecondary);
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
  public void handleContact(SolObj other, ContactImpulse impulse, boolean isA, float absImpulse,
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
    private final PathLoader myLoader;

    public Builder() {
      myLoader = new PathLoader("misc");
    }

    public StarPort build(SolGame game, Planet from, Planet to, boolean secondary) {
      float angle = SolMath.angle(from.getPos(), to.getPos());
      Vector2 pos = getDesiredPos(from, to, false);
      ArrayList<Dra> dras = new ArrayList<Dra>();
      Body body = myLoader.getBodyAndSprite(game, "misc", "starPort", SIZE,
        BodyDef.BodyType.KinematicBody, new Vector2(pos), angle, dras, 10f, DraLevel.BIG_BODIES, null);
      SolMath.free(pos);
      StarPort sp = new StarPort(from, to, body, dras, secondary);
      body.setUserData(sp);
      return sp;
    }
  }

  public static class MyFar implements FarObj {
    private final Planet myFrom;
    private final Planet myTo;
    private final Vector2 myPos;
    private final float myRadius;
    private final boolean mySecondary;
    private float myAngle;

    public MyFar(Planet from, Planet to, Vector2 pos, float radius, boolean secondary) {
      myFrom = from;
      myTo = to;
      myPos = new Vector2(pos);
      myRadius = radius;
      mySecondary = secondary;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
      return false;
    }

    @Override
    public SolObj toObj(SolGame game) {
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
      return myRadius;
    }

    @Override
    public Vector2 getPos() {
      return myPos;
    }

    @Override
    public String toDebugString() {
      return null;
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

  public static class Transcendent implements SolObj {
    private static final float TRAN_SZ = 1f;
    private final Planet myFrom;
    private final Planet myTo;
    private final Vector2 myPos;
    private final float myRadius;
    private final Vector2 myDestPos;
    private final ArrayList<Dra> myDras;
    private final FarShip myShip;

    private float myAngle;

    public Transcendent(TexMan texMan, SolShip ship, Planet from, Planet to) {
      myShip = ship.toFarObj();
      myFrom = from;
      myTo = to;
      myPos = new Vector2(ship.getPos());
      myDestPos = new Vector2();

      RectSprite s = new RectSprite(texMan.getTex("misc/transcendent", null), TRAN_SZ, TRAN_SZ, 0, new Vector2(), DraLevel.BODIES, 0, 0, Col.W);
      myDras = new ArrayList<Dra>();
      myDras.add(s);
      myRadius = DraMan.radiusFromDras(myDras);
    }

    public FarShip getShip() {
      return myShip;
    }

    @Override
    public void update(SolGame game) {
      float ts = game.getTimeStep();

      Vector2 toPos = myTo.getPos();
      float nodeAngle = SolMath.angle(toPos, myFrom.getPos());
      SolMath.fromAl(myDestPos, nodeAngle, myTo.getFullHeight() + DIST_FROM_PLANET + SIZE/2);
      myDestPos.add(toPos);


      myAngle = SolMath.angle(myPos, myDestPos);
      Vector2 moveDiff = SolMath.fromAl(myAngle, Const.MAX_MOVE_SPD * 2); //hack again : (
      moveDiff.scl(ts);
      myPos.add(moveDiff);
      SolMath.free(moveDiff);

      if (myPos.dst(myDestPos) < .5f) {
        ObjMan objMan = game.getObjMan();
        objMan.removeObjDelayed(this);
        myShip.setPos(myPos);
        myShip.setSpd(new Vector2());
        SolObj ship = myShip.toObj(game);
        objMan.addObjDelayed(ship);
      }
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
      return false;
    }

    @Override
    public void onRemove(SolGame game) {
    }

    @Override
    public float getRadius() {
      return myRadius;
    }

    @Override
    public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
      game.getSpecialSounds().playDmg(game, this, pos, dmgType);
    }

    @Override
    public boolean receivesGravity() {
      return false;
    }

    @Override
    public void receiveAcc(Vector2 acc, SolGame game) {
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
      return null;
    }

    @Override
    public void handleContact(SolObj other, ContactImpulse impulse, boolean isA, float absImpulse,
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
  }
}
