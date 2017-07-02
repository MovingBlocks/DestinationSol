/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game.planet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import org.destinationsol.Const;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;

import java.util.ArrayList;
import java.util.List;

public class TileObjBuilder {
    public TileObject build(SolGame game, float sz, float toPlanetRelAngle, float dist, Tile tile, Planet planet) {
        float spriteSz = sz * 2;
        RectSprite sprite = new RectSprite(tile.tex, spriteSz, 0, 0, new Vector2(), DrawableLevel.GROUND, 0, 0f, SolColor.WHITE, false);
        Body body = null;
        if (tile.points.size() > 0) {
            body = buildBody(game, toPlanetRelAngle, dist, tile, planet, spriteSz);
        }
        TileObject res = new TileObject(planet, toPlanetRelAngle, dist, sz, sprite, body, tile);
        if (body != null) {
            body.setUserData(res);
        }
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
        List<Vector2> points = new ArrayList<>();
        for (Vector2 curr : tile.points) {
            Vector2 v = new Vector2(curr);
            v.scl(spriteSz);
            points.add(v);
        }
        Vector2[] v = points.toArray(new Vector2[] {});
        shape.createLoop(v);
        Fixture f = body.createFixture(shape, 0);
        f.setFriction(Const.FRICTION);
        shape.dispose();
        return body;
    }
}
