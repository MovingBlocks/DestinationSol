/*
 * Copyright 2019 MovingBlocks
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
package org.destinationsol.menu.background;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColorUtil;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public class MenuBackgroundAsteroidManager {
    private final DisplayDimensions displayDimensions;
    private final CollisionMeshLoader asteroidMeshLoader;

    private List<TextureAtlas.AtlasRegion> availableAsteroidTextures;
    private List<MenuBackgroundObject> backgroundAsteroids;
    private List<MenuBackgroundObject> retainedBackgroundAsteroids;

    private World world;

    private final int NUMBER_OF_ASTEROIDS = 10;

    public MenuBackgroundAsteroidManager(DisplayDimensions displayDimensions, World world) {
        this.displayDimensions = displayDimensions;
        this.world = world;

        asteroidMeshLoader = new CollisionMeshLoader("engine:asteroids");

        availableAsteroidTextures = Assets.listTexturesMatching("engine:asteroid_.*");
        backgroundAsteroids = new ArrayList<>();
        retainedBackgroundAsteroids = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_ASTEROIDS; i++) {
            backgroundAsteroids.add(buildAsteroid());
        }
    }

    public void update() {
        retainedBackgroundAsteroids.clear();

        float radius = (float) Math.sqrt(0.25 + Math.pow(displayDimensions.getRatio() / 2, 2));
        for (MenuBackgroundObject backgroundObject : backgroundAsteroids) {
            backgroundObject.update();

            float distance = (float) Math.sqrt(Math.pow(backgroundObject.getPosition().x - displayDimensions.getRatio() / 2, 2) + Math.pow(backgroundObject.getPosition().y - 0.5f, 2));
            if (distance < radius) {
                retainedBackgroundAsteroids.add(backgroundObject);
            } else {
                retainedBackgroundAsteroids.add(buildAsteroid());
            }
        }
        backgroundAsteroids.clear();
        backgroundAsteroids.addAll(retainedBackgroundAsteroids);
    }

    public void draw(UiDrawer uiDrawer) {
        for (MenuBackgroundObject backgroundObject : backgroundAsteroids) {
            backgroundObject.draw(uiDrawer);
        }
    }

    public MenuBackgroundObject buildAsteroid() {
        TextureAtlas.AtlasRegion texture = SolRandom.randomElement(availableAsteroidTextures);

        boolean small = SolRandom.test(.8f);
        float size = (small ? .2f : .4f) * SolRandom.randomFloat(.5f, 1);
        Color tint = new Color();
        SolColorUtil.fromHSB(SolRandom.randomFloat(0, 1), .25f, 1, .7f, tint);

        float radiusX = (float) (texture.originalHeight) / displayDimensions.getWidth() * size / 2;
        float radiusY = (float) (texture.originalHeight) / displayDimensions.getHeight() * size / 2;

        float r = displayDimensions.getRatio();
        Vector2 velocity, position;
        if (SolRandom.test(0.5f)) {
            // Spawn from the left or right of screen
            boolean fromLeft = SolRandom.test(0.5f);
            velocity = new Vector2((fromLeft ? 1 : -1) * (float) Math.pow(SolRandom.randomFloat(0.025f, 0.1f), 2), (float) Math.pow(SolRandom.randomFloat(0.095f), 2));
            position = new Vector2(r / 2 + (fromLeft ? -1 : 1) * (r / 2 + radiusX) - radiusX, 0.5f + SolRandom.randomFloat(0.5f + radiusY) - radiusY);
        } else {
            // Spawn from the top or bottom of screen
            boolean fromTop = SolRandom.test(0.5f);
            velocity = new Vector2((float) Math.pow(SolRandom.randomFloat(0.095f), 3), (fromTop ? 1 : -1) * (float) Math.pow(SolRandom.randomFloat(-0.025f, -0.1f), 2));
            position = new Vector2(r / 2 + SolRandom.randomFloat(r / 2 + radiusX) - radiusX, 0.5f + (fromTop ? -1 : 1) * (0.5f + radiusY) - radiusY);
        }

        //Give random rotation to asteroid
        float angle = SolRandom.randomFloat((float) Math.PI);
        float angularVelocity = SolRandom.randomFloat(1.5f);
        velocity.scl(50);

        //Build the final asteroid body
        Body body = asteroidMeshLoader.getBodyAndSprite(world, texture, size * 0.95f, BodyDef.BodyType.DynamicBody, position, angle, new ArrayList<>(), 10f, DrawableLevel.BODIES);
        body.setLinearVelocity(velocity);
        body.setAngularVelocity(angularVelocity);
        MenuBackgroundObject asteroid = new MenuBackgroundObject(texture, size, tint, position, velocity, asteroidMeshLoader.getOrigin(texture.name, size).cpy(), angle, body);
        body.setUserData(asteroid);

        return asteroid;
    }
}
