/*
 * Copyright 2018 MovingBlocks
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

package org.destinationsol.game.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.particle.LightSource;
import org.destinationsol.game.ship.SolShip;

import java.util.ArrayList;
import java.util.List;

public class LootBuilder {

    public LootBuilder() {
    }

    // set velocity & rot speed
    public Loot build(SolGame game, Vector2 position, SolItem item, Vector2 velocity, int life, float rotationSpeed, SolShip owner) {
        List<Drawable> drawables = new ArrayList<>();
        TextureAtlas.AtlasRegion tex = item.getIcon(game);
        float sz = item.getItemType().sz;
        RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DrawableLevel.GUNS, 0, 0, SolColor.WHITE, false);
        drawables.add(s);
        Body b = buildBody(game, position, sz);
        b.setLinearVelocity(velocity);
        b.setAngularVelocity(rotationSpeed);
        Color col = item.getItemType().color;
        LightSource ls = new LightSource(sz + .18f, false, .5f, new Vector2(), col);
        ls.collectDrawables(drawables);
        Loot loot = new Loot(item, b, life, drawables, ls, owner);
        b.setUserData(loot);
        return loot;
    }

    private Body buildBody(SolGame game, Vector2 position, float sz) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.angle = 0;
        bd.angularDamping = 0;
        bd.position.set(position);
        bd.linearDamping = 0;
        Body body = game.getObjectManager().getWorld().createBody(bd);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sz / 2, sz / 2);
        body.createFixture(shape, .5f);
        shape.dispose();
        return body;
    }
}
