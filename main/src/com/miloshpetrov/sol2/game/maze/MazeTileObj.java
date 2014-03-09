package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.*;

import java.util.ArrayList;
import java.util.List;

public class MazeTileObj implements SolObj {
  private final List<Dra> myDras;
  private final Body myBody;
  private final Vector2 myPos;
  private final float myAngle;
  private final float myRadius;
  private final MazeTile myTile;

  public MazeTileObj(MazeTile tile, List<Dra> dras, Body body, Vector2 pos, float angle) {
    myTile = tile;
    myDras = dras;
    myBody = body;
    myPos = pos;
    myAngle = angle;
    myRadius = DraMan.radiusFromDras(dras);
  }

  @Override
  public void update(SolGame game) {
  }

  @Override
  public boolean shouldBeRemoved(SolGame game) {
    return false;
  }

  @Override
  public void onRemove(SolGame game) {
    if (myBody != null) myBody.getWorld().destroyBody(myBody);
  }

  @Override
  public float getRadius() {
    return myRadius;
  }

  @Override
  public void receiveDmg(float dmg, SolGame game, Vector2 pos, DmgType dmgType) {
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
    return new MyFar(myTile, myAngle, myPos, myRadius);
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
  public void handleContact(SolObj other, Contact contact, ContactImpulse impulse, boolean isA, float absImpulse,
    SolGame game)
  {
  }

  @Override
  public String toDebugString() {
    return null;
  }

  private static class MyFar implements FarObj {

    private final MazeTile myTile;
    private final float myAngle;
    private final Vector2 myPos;
    private final float myRadius;

    public MyFar(MazeTile tile, float angle, Vector2 pos, float radius) {
      myTile = tile;
      myAngle = angle;
      myPos = pos;
      myRadius = radius;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
      return false;
    }

    @Override
    public SolObj toObj(SolGame game) {
      return new Builder().build(game, myTile, myPos, myAngle);
    }

    @Override
    public void update(SolGame game) {
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
  }

  public static class Builder {
    public MazeTileObj build(SolGame game, MazeTile tile, Vector2 pos, float angle) {
      List<Dra> dras = new ArrayList<Dra>();
      RectSprite s = new RectSprite(tile.tex, MazeBuilder.TILE_SZ, 0, 0, new Vector2(), DraLevel.DECO, 0, 0, Col.W);
      dras.add(s);
      Body body = buildBody(game, angle, pos, tile);
      MazeTileObj res = new MazeTileObj(tile, dras, body, pos, angle);
      body.setUserData(res);
      return res;
    }

    private Body buildBody(SolGame game, float angle, Vector2 pos, MazeTile tile) {
      BodyDef def = new BodyDef();
      def.type = BodyDef.BodyType.KinematicBody;
      def.position.set(pos);
      def.angle = angle * SolMath.degRad;
      def.angularDamping = 0;
      Body body = game.getObjMan().getWorld().createBody(def);

      for (List<Vector2> pts : tile.points) {
        ChainShape shape = new ChainShape();
        List<Vector2> points  = new ArrayList<Vector2>();
        for (Vector2 curr : pts) {
          Vector2 v = new Vector2(curr);
          v.add(-.5f, -.5f);
          v.scl(MazeBuilder.TILE_SZ);
          points.add(v);
        }
        Vector2[] v = points.toArray(new Vector2[]{});
        shape.createLoop(v);
        Fixture f = body.createFixture(shape, 0);
        f.setFriction(Const.FRICTION);
        shape.dispose();
      }

      return body;
    }
  }
}