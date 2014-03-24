package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
  private final boolean myFlipped;

  public MazeTileObj(MazeTile tile, List<Dra> dras, Body body, Vector2 pos, float angle, boolean flipped) {
    myTile = tile;
    myDras = dras;
    myBody = body;
    myPos = pos;
    myAngle = angle;
    myRadius = DraMan.radiusFromDras(dras);
    myFlipped = flipped;
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
    return new MyFar(myTile, myAngle, myPos, myRadius, myFlipped);
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
    return myTile.metal;
  }

  private static class MyFar implements FarObj {

    private final MazeTile myTile;
    private final float myAngle;
    private final Vector2 myPos;
    private final float myRadius;
    private final boolean myFlipped;

    public MyFar(MazeTile tile, float angle, Vector2 pos, float radius, boolean flipped) {
      myTile = tile;
      myAngle = angle;
      myPos = pos;
      myRadius = radius;
      myFlipped = flipped;
    }

    @Override
    public boolean shouldBeRemoved(SolGame game) {
      return false;
    }

    @Override
    public SolObj toObj(SolGame game) {
      return new Builder().build(game, myTile, myPos, myAngle, myFlipped);
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
    public MazeTileObj build(SolGame game, MazeTile tile, Vector2 pos, float angle, boolean flipped) {
      List<Dra> dras = new ArrayList<Dra>();
      TextureAtlas.AtlasRegion tex = tile.tex;
      if (flipped) tex = game.getTexMan().getFlipped(tex);
      RectSprite s = new RectSprite(tex, MazeBuilder.TILE_SZ, 0, 0, new Vector2(), DraLevel.DECO, 0, 0, Col.W);
      dras.add(s);
      Body body = buildBody(game, angle, pos, tile, flipped);
      MazeTileObj res = new MazeTileObj(tile, dras, body, pos, angle, flipped);
      body.setUserData(res);
      return res;
    }

    private Body buildBody(SolGame game, float angle, Vector2 pos, MazeTile tile, boolean flipped) {
      BodyDef def = new BodyDef();
      def.type = BodyDef.BodyType.KinematicBody;
      def.position.set(pos);
      def.angle = angle * SolMath.degRad;
      def.angularDamping = 0;
      Body body = game.getObjMan().getWorld().createBody(def);

      for (List<Vector2> pts : tile.points) {
        ChainShape shape = new ChainShape();
        List<Vector2> points  = new ArrayList<Vector2>();
        int sz = pts.size();
        for (int i = 0; i < sz; i++) {
          Vector2 curr = pts.get(flipped ? sz - i - 1 : i);
          Vector2 v = new Vector2(curr);
          v.add(-.5f, -.5f);
          if (flipped) v.x *= -1;
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