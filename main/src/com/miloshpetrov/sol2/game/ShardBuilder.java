package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.asteroid.AsteroidBuilder;
import com.miloshpetrov.sol2.game.dra.Dra;
import com.miloshpetrov.sol2.game.dra.DraLevel;
import com.miloshpetrov.sol2.game.ship.ShipBuilder;

import java.util.ArrayList;

public class ShardBuilder {
  private static final float MAX_ROT_SPD = 5f;
  private static final float MAX_SPD = 4f;
  public static final float MIN_SCALE = .07f;
  public static final float MAX_SCALE = .12f;
  public static final float SIZE_TO_SHARD_COUNT = 13f;

  private final PathLoader myPathLoader;
  private final ArrayList<TextureAtlas.AtlasRegion> myTexs;

  public ShardBuilder(TexMan texMan) {
    myPathLoader = new PathLoader("shards");
    myTexs = texMan.getPack("shards/shard", null);
  }

  public void buildExplosionShards(SolGame game, Vector2 pos, Vector2 baseSpd, float size) {
    int count = (int) (size * SIZE_TO_SHARD_COUNT);
    for (int i = 0; i < count; i++) {
      Shard s = build(game, pos, baseSpd, size);
      game.getObjMan().addObjDelayed(s);
    }
  }

  public Shard build(SolGame game, Vector2 basePos, Vector2 baseSpd, float size) {

    ArrayList<Dra> dras = new ArrayList<Dra>();
    float scale = SolMath.rnd(MIN_SCALE, MAX_SCALE);
    TextureAtlas.AtlasRegion tex = SolMath.elemRnd(myTexs);
    float spdAngle = SolMath.rnd(180);
    Vector2 pos = new Vector2();
    SolMath.fromAl(pos, spdAngle, SolMath.rnd(size));
    pos.add(basePos);
    Body body = myPathLoader.getBodyAndSprite(game, "shards", AsteroidBuilder.removePath(tex.name) + "_" + tex.index, scale,
      BodyDef.BodyType.DynamicBody, pos, SolMath.rnd(180), dras, ShipBuilder.SHIP_DENSITY, DraLevel.BODIES, null);

    body.setAngularVelocity(SolMath.rnd(MAX_ROT_SPD));
    Vector2 spd = SolMath.fromAl(spdAngle, SolMath.rnd(MAX_SPD));
    spd.add(baseSpd);
    body.setLinearVelocity(spd);
    SolMath.free(spd);

    Shard shard = new Shard(body, dras);
    body.setUserData(shard);
    return shard;
  }
}
