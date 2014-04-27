package com.miloshpetrov.sol2.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.dra.*;

import java.util.ArrayList;
import java.util.List;

public class TileObjBuilder {
  public TileObj build(SolGame game, float sz, float toPlanetRelAngle, float dist, Tile tile, Planet planet) {
    float spriteSz = sz * 2;
    RectSprite sprite = new RectSprite(tile.tex, spriteSz, 0, 0, new Vector2(), DraLevel.GROUND, 0, 0f, Col.W, false);
    Body body = null;
    if (tile.points.size() > 0) {
      body = buildBody(game, toPlanetRelAngle, dist, tile, planet, spriteSz);
    }
    TileObj res = new TileObj(planet, toPlanetRelAngle, dist, sz, sprite, body, tile);
    if (body != null) body.setUserData(res);
    return res;
  }

  private Body buildBody(SolGame game, float toPlanetRelAngle, float dist, Tile tile, Planet planet, float spriteSz) {
    BodyDef def = new BodyDef();
    def.type = BodyDef.BodyType.KinematicBody;
    float toPlanetAngle = planet.getAngle() + toPlanetRelAngle;
    SolMath.fromAl(def.position, toPlanetAngle, dist, true);
    def.position.add(planet.getPos());
    def.angle = (toPlanetAngle + 90) * SolMath.degRad;
    def.angularDamping = 0;
    Body body = game.getObjMan().getWorld().createBody(def);
    ChainShape shape = new ChainShape();
    List<Vector2> points  = new ArrayList<Vector2>();
    for (Vector2 curr : tile.points) {
      Vector2 v = new Vector2(curr);
      v.scl(spriteSz);
      points.add(v);
    }
    Vector2[] v = points.toArray(new Vector2[]{});
    shape.createLoop(v);
    Fixture f = body.createFixture(shape, 0);
    f.setFriction(Const.FRICTION);
    shape.dispose();
    return body;
  }
}
