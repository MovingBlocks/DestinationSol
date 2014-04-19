package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.common.Col;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.particle.LightSrc;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class LootBuilder {
  public static final float SZ = .12f;

  public LootBuilder() {
  }

  // set spd & rot spd
  public Loot build(SolGame game, Vector2 pos, SolItem item, Vector2 spd, int life, float rotSpd, SolShip owner) {
    List<Dra> dras = new ArrayList<Dra>();
    TextureAtlas.AtlasRegion tex = item.getIcon(game);
    RectSprite s = new RectSprite(tex, SZ, 0, 0, new Vector2(), DraLevel.GUNS, 0, 0, Col.W, false);
    dras.add(s);
    Body b = buildBody(game, pos);
    b.setLinearVelocity(spd);
    b.setAngularVelocity(rotSpd);
    LightSrc ls = new LightSrc(game, .3f, false, .5f, new Vector2(), Col.W);
    ls.collectDras(dras);
    Loot loot = new Loot(item, b, life, dras, ls, owner);
    b.setUserData(loot);
    return loot;
  }

  private Body buildBody(SolGame game, Vector2 pos) {
    BodyDef bd = new BodyDef();
    bd.type = BodyDef.BodyType.DynamicBody;
    bd.angle = 0;
    bd.angularDamping = 0;
    bd.position.set(pos);
    bd.linearDamping = 0;
    Body body = game.getObjMan().getWorld().createBody(bd);
    PolygonShape shape = new PolygonShape();
    shape.setAsBox(SZ/2, SZ/2);
    body.createFixture(shape, .5f);
    shape.dispose();
    return body;
  }
}
