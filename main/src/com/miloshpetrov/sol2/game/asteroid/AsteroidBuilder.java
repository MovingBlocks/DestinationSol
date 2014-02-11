package com.miloshpetrov.sol2.game.asteroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraLevel;

import java.util.ArrayList;

public class AsteroidBuilder {
  public static final int VARIANT_COUNT = 2;
  private static final float MAX_ROT_SPD = .5f;
  private static final float MAX_SPD = .2f;
  public static final float MIN_SCALE = .5f;
  public static final float MAX_SCALE = 1.5f;

  private final PathLoader myPathLoader;

  public AsteroidBuilder() {
    myPathLoader = new PathLoader(Gdx.files.internal("res/paths/asteroids.json"));
  }

  // doesn't consume pos
  public Asteroid build(SolGame game, Vector2 pos, int modelNr, RemoveController removeController) {

    ArrayList<Dra> dras = new ArrayList<Dra>();
    float scale = SolMath.rnd(MIN_SCALE, MAX_SCALE);
    Body body = myPathLoader.getBodyAndSprite(game, "asteroids", String.valueOf(modelNr), scale,
      BodyDef.BodyType.DynamicBody, pos, SolMath.rnd(180), dras, 10f, DraLevel.BODIES);

    body.setAngularVelocity(SolMath.rnd(MAX_ROT_SPD));
    Vector2 spd = SolMath.fromAl(SolMath.rnd(180), MAX_SPD);
    body.setLinearVelocity(spd);
    SolMath.free(spd);

    Asteroid res = new Asteroid(modelNr, body, removeController, dras);
    body.setUserData(res);
    return res;
  }
}
