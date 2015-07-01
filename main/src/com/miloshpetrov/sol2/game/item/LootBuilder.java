/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 package com.miloshpetrov.sol2.game.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.miloshpetrov.sol2.common.SolColor;
import com.miloshpetrov.sol2.game.SolGame;
import com.miloshpetrov.sol2.game.dra.*;
import com.miloshpetrov.sol2.game.particle.LightSrc;
import com.miloshpetrov.sol2.game.ship.SolShip;

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
