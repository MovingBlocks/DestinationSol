package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraLevel;
import com.miloshpetrov.sol2.game.ship.ShipBuilder;

import java.util.ArrayList;

public class ShardBuilder {
  public static final int VARIANT_COUNT = 3;
  private static final float MAX_ROT_SPD = 5f;
  private static final float MAX_SPD = 4f;
  public static final float MIN_SCALE = .07f;
  public static final float MAX_SCALE = .12f;
  public static final float SIZE_TO_SHARD_COUNT = 13f;

  private final PathLoader myPathLoader;

  public ShardBuilder() {
    myPathLoader = new PathLoader(Gdx.files.internal("res/paths/shards.json"));
  }

  public void buildExplosionShards(SolGame game, Vector2 pos, Vector2 baseSpd, float size) {
    int count = (int) (size * SIZE_TO_SHARD_COUNT);
    for (int i = 0; i < count; i++) {
      Shard s = build(game, pos, baseSpd);
      game.getObjMan().addObjDelayed(s);
    }
  }

  public Shard build(SolGame game, Vector2 pos, Vector2 baseSpd) {

    int modelNr = SolMath.intRnd(VARIANT_COUNT);
    ArrayList<Dra> dras = new ArrayList<Dra>();
    float scale = SolMath.rnd(MIN_SCALE, MAX_SCALE);
    Body body = myPathLoader.getBodyAndSprite(game, "shards", String.valueOf(modelNr), scale,
      BodyDef.BodyType.DynamicBody, pos, SolMath.rnd(180), dras, ShipBuilder.SHIP_DENSITY, DraLevel.BODIES);

    body.setAngularVelocity(SolMath.rnd(MAX_ROT_SPD));
    Vector2 spd = SolMath.fromAl(SolMath.rnd(180), MAX_SPD);
    spd.add(baseSpd);
    body.setLinearVelocity(spd);
    SolMath.free(spd);

    return new Shard(body, dras);
  }
}
