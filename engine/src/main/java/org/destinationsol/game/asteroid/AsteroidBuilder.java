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
package org.destinationsol.game.asteroid;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolRandom;
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

    public static Body buildBall(SolGame game, Vector2 position, float angle, float rad, float density, boolean sensor) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.angle = angle * MathUtils.degRad;
        bodyDef.angularDamping = 0;
        bodyDef.position.set(position);
        bodyDef.linearDamping = 0;
        Body body = game.getObjectManager().getWorld().createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.friction = Const.FRICTION;
        fixtureDef.shape = new CircleShape();
        fixtureDef.shape.setRadius(rad);
        fixtureDef.isSensor = sensor;
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();
        return body;
    }

    // doesn't consume position
    public Asteroid buildNew(SolGame game, Vector2 position, Vector2 velocity, float size, RemoveController removeController) {
        float rotationSpeed = SolRandom.randomFloat(MAX_A_ROT_SPD);
        return build(game, position, SolRandom.randomElement(textures), size, SolRandom.randomFloat(180), rotationSpeed, velocity, removeController);
    }

    // doesn't consume position
    public FarAsteroid buildNewFar(Vector2 position, Vector2 velocity, float size, RemoveController removeController) {
        float rotationSpeed = SolRandom.randomFloat(MAX_A_ROT_SPD);
        return new FarAsteroid(SolRandom.randomElement(textures), new Vector2(position), SolRandom.randomFloat(180), removeController, size, new Vector2(velocity), rotationSpeed);
    }

    // doesn't consume position
    public Asteroid build(SolGame game, Vector2 position, TextureAtlas.AtlasRegion texture, float size, float angle, float rotationSpeed, Vector2 velocity, RemoveController removeController) {

        ArrayList<Drawable> drawables = new ArrayList<>();
        Body body;
        if (MAX_BALL_SZ < size) {
            body = collisionMeshLoader.getBodyAndSprite(game, texture, size, BodyDef.BodyType.DynamicBody, position, angle, drawables, DENSITY, DrawableLevel.BODIES);
        } else {
            body = buildBall(game, position, angle, size / 2, DENSITY, false);
            RectSprite s = new RectSprite(texture, size, 0, 0, new Vector2(), DrawableLevel.BODIES, 0, 0, SolColor.WHITE, false);
            drawables.add(s);
        }
        body.setAngularVelocity(rotationSpeed);
        body.setLinearVelocity(velocity);

        Asteroid asteroid = new Asteroid(game, texture, body, size, removeController, drawables);
        body.setUserData(asteroid);
        return asteroid;
    }
}
