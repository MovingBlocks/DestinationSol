package com.miloshpetrov.sol2.game.asteroid;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.*;

import java.util.ArrayList;

public class AsteroidBuilder {
  private static final float MAX_A_ROT_SPD = .5f;
  private static final float MAX_BALL_SZ = .2f;
  public static final float DENSITY = 10f;

  private final PathLoader myPathLoader;
  private final ArrayList<TextureAtlas.AtlasRegion> myTexs;

  public AsteroidBuilder(TexMan texMan) {
    myPathLoader = new PathLoader("asteroids");
    myTexs = texMan.getPack("asteroids/sys", null);
  }

  // doesn't consume pos
  public Asteroid buildNew(SolGame game, Vector2 pos, Vector2 spd, float sz, RemoveController removeController) {
    float rotSpd = SolMath.rnd(MAX_A_ROT_SPD);
    return build(game, pos, SolMath.elemRnd(myTexs), sz, SolMath.rnd(180), rotSpd, spd, removeController);
  }

  // doesn't consume pos
  public Asteroid build(SolGame game, Vector2 pos, TextureAtlas.AtlasRegion tex, float sz, float angle, float rotSpd, Vector2 spd, RemoveController removeController) {

    ArrayList<Dra> dras = new ArrayList<Dra>();
    Body body;
    if (MAX_BALL_SZ < sz) {
      body = myPathLoader.getBodyAndSprite(game, "asteroids", removePath(tex.name) + "_" + tex.index, sz,
        BodyDef.BodyType.DynamicBody, pos, angle, dras, DENSITY, DraLevel.BODIES, tex);
    } else {
      body = buildBall(game, pos, angle, sz/2, DENSITY);
      RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DraLevel.BODIES, 0, 0, Col.W);
      dras.add(s);
    }
    body.setAngularVelocity(rotSpd);
    body.setLinearVelocity(spd);

    Asteroid res = new Asteroid(tex, body, sz, removeController, dras);
    body.setUserData(res);
    return res;
  }

  public static String removePath(String name) {
    String[] parts = name.split("[/\\\\]");
    return parts[parts.length - 1];
  }

  public static Body buildBall(SolGame game, Vector2 pos, float angle, float rad, float density) {
    BodyDef bd = new BodyDef();
    bd.type = BodyDef.BodyType.DynamicBody;
    bd.angle = angle * SolMath.degRad;
    bd.angularDamping = 0;
    bd.position.set(pos);
    bd.linearDamping = 0;
    Body body = game.getObjMan().getWorld().createBody(bd);
    FixtureDef fd = new FixtureDef();
    fd.density = density;
    fd.friction = Const.FRICTION;
    fd.shape = new CircleShape();
    fd.shape.setRadius(rad);
    body.createFixture(fd);
    fd.shape.dispose();
    return body;
  }
}
