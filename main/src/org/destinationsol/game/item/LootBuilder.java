package org.destinationsol.game.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.dra.Dra;
import org.destinationsol.game.dra.DraLevel;
import org.destinationsol.game.dra.RectSprite;
import org.destinationsol.game.particle.LightSrc;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class LootBuilder {

  public LootBuilder() {
  }

  // set spd & rot spd
  public Loot build(SolGame game, Vector2 pos, SolItem item, Vector2 spd, int life, float rotSpd, SolShip owner) {
    List<Dra> dras = new ArrayList<Dra>();
    TextureAtlas.AtlasRegion tex = item.getIcon(game);
    float sz = item.getItemType().sz;
    RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DraLevel.GUNS, 0, 0, SolColor.W, false);
    dras.add(s);
    Body b = buildBody(game, pos, sz);
    b.setLinearVelocity(spd);
    b.setAngularVelocity(rotSpd);
    Color col = item.getItemType().color;
    LightSrc ls = new LightSrc(game, sz + .18f, false, .5f, new Vector2(), col);
    ls.collectDras(dras);
    Loot loot = new Loot(item, b, life, dras, ls, owner);
    b.setUserData(loot);
    return loot;
  }

  private Body buildBody(SolGame game, Vector2 pos, float sz) {
    BodyDef bd = new BodyDef();
    bd.type = BodyDef.BodyType.DynamicBody;
    bd.angle = 0;
    bd.angularDamping = 0;
    bd.position.set(pos);
    bd.linearDamping = 0;
    Body body = game.getObjMan().getWorld().createBody(bd);
    PolygonShape shape = new PolygonShape();
    shape.setAsBox(sz /2, sz /2);
    body.createFixture(shape, .5f);
    shape.dispose();
    return body;
  }
}
