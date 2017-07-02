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
package org.destinationsol.game.asteroid;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.RemoveController;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;

import java.util.ArrayList;
import java.util.List;

public class AsteroidBuilder {
    private static final float DENSITY = 10f;
    private static final float MAX_A_ROT_SPD = .5f;
    private static final float MAX_BALL_SZ = .2f;
    private final CollisionMeshLoader collisionMeshLoader;
    private final List<TextureAtlas.AtlasRegion> textures;

    public AsteroidBuilder() {
        collisionMeshLoader = new CollisionMeshLoader("engine:asteroids");
        textures = Assets.listTexturesMatching("engine:asteroid_.*");
    }

    public static Body buildBall(SolGame game, Vector2 pos, float angle, float rad, float density, boolean sensor) {
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
        fd.isSensor = sensor;
        body.createFixture(fd);
        fd.shape.dispose();
        return body;
    }

    // doesn't consume pos
    public Asteroid buildNew(SolGame game, Vector2 pos, Vector2 spd, float sz, RemoveController removeController) {
        float rotSpd = SolMath.rnd(MAX_A_ROT_SPD);
        return build(game, pos, SolMath.elemRnd(textures), sz, SolMath.rnd(180), rotSpd, spd, removeController);
    }

    // doesn't consume pos
    public FarAsteroid buildNewFar(Vector2 pos, Vector2 spd, float sz, RemoveController removeController) {
        float rotSpd = SolMath.rnd(MAX_A_ROT_SPD);
        return new FarAsteroid(SolMath.elemRnd(textures), new Vector2(pos), SolMath.rnd(180), removeController, sz, new Vector2(spd), rotSpd);
    }

    // doesn't consume pos
    public Asteroid build(SolGame game, Vector2 pos, TextureAtlas.AtlasRegion tex, float sz, float angle, float rotSpd, Vector2 spd, RemoveController removeController) {

        ArrayList<Drawable> drawables = new ArrayList<>();
        Body body;
        if (MAX_BALL_SZ < sz) {
            body = collisionMeshLoader.getBodyAndSprite(game, tex, sz, BodyDef.BodyType.DynamicBody, pos, angle, drawables, DENSITY, DrawableLevel.BODIES);
        } else {
            body = buildBall(game, pos, angle, sz / 2, DENSITY, false);
            RectSprite s = new RectSprite(tex, sz, 0, 0, new Vector2(), DrawableLevel.BODIES, 0, 0, SolColor.WHITE, false);
            drawables.add(s);
        }
        body.setAngularVelocity(rotSpd);
        body.setLinearVelocity(spd);

        Asteroid res = new Asteroid(game, tex, body, sz, removeController, drawables);
        body.setUserData(res);
        return res;
    }
}
