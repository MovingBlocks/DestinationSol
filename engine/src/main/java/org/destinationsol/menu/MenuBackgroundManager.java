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
package org.destinationsol.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.Const;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolColorUtil;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.drawables.Drawable;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.game.drawables.RectSprite;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.destinationsol.ui.DisplayDimensions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuBackgroundManager {
    private final World world;

    private final DisplayDimensions displayDimensions;
    private final MenuMeshLoader asteroidMeshLoader;

    public MenuBackgroundManager(String fileName, DisplayDimensions displayDimensions) {
        world = new World(new Vector2(0, 0), true);
        MenuContactListener contactListener = new MenuContactListener();
        world.setContactListener(contactListener);
        this.displayDimensions = displayDimensions;

        asteroidMeshLoader = new MenuMeshLoader("engine:asteroids", world);
    }

    public BackgroundAsteroid buildAsteroid() {

        TextureAtlas.AtlasRegion texture = Assets.getAtlasRegion(SolRandom.test(0.5f) ? "engine:asteroid_0" : "engine:asteroid_1");

        boolean small = SolRandom.test(.8f);
        float scale = (small ? .1f : .4f) * SolRandom.randomFloat(.5f, 1);
        Color tint = new Color();
        SolColorUtil.fromHSB(SolRandom.randomFloat(0, 1), .25f, 1, .7f, tint);

        float radiusX = (float) (texture.originalHeight) / displayDimensions.getWidth() * scale / 2;
        float radiusY = (float) (texture.originalHeight) / displayDimensions.getHeight() * scale / 2;

        float r = displayDimensions.getRatio();
        Vector2 velocity, position;
        if (SolRandom.test(0.5f)) {
            // Spawn to the left or right of screen
            boolean toLeft = SolRandom.test(1f);
            velocity = new Vector2((float) Math.pow(SolRandom.randomFloat(toLeft ? 0.025f : -0.1f, toLeft ? 0.1f : 0.025f), 2), (float) Math.pow(SolRandom.randomFloat(0.095f), 2));
            position = new Vector2(r / 2 + (toLeft ? -1 : 1) * (r / 2 + radiusX) - radiusX, 0.5f + SolRandom.randomFloat(0.5f + radiusY) - radiusY);
        } else {
            // Spawn at the top or bottom of screen
            boolean atTop = SolRandom.test(1f);
            velocity = new Vector2((float) Math.pow(SolRandom.randomFloat(0.095f), 3), (float) Math.pow(SolRandom.randomFloat(atTop ? -0.025f : 0.025f, atTop ? -0.1f : 0.1f), 2));
            position = new Vector2(r / 2 + SolRandom.randomFloat(r / 2 + radiusX) - radiusX, 0.5f + (atTop ? -1 : 1) * (0.5f + radiusY) - radiusY);
        }

        float angle = SolRandom.randomFloat((float) Math.PI);
        float angularVelocity = SolRandom.randomFloat(1.5f);
        velocity.scl(50);

        Body body = asteroidMeshLoader.getBodyAndSprite(texture, scale*0.89f, BodyDef.BodyType.DynamicBody, position, angle, new ArrayList<>(), 10f, DrawableLevel.BODIES);
        body.setLinearVelocity(velocity);
        body.setAngularVelocity(angularVelocity);
        BackgroundAsteroid asteroid = new BackgroundAsteroid(texture, scale, tint, position, velocity, angle, angularVelocity, body);
        body.setUserData(asteroid);

        return asteroid;
    }


    public void update() {
        world.step(Const.REAL_TIME_STEP, 6, 2);
    }

}
