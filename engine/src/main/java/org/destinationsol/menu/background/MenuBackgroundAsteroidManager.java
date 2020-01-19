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
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.CommonDrawer;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColorUtil;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.ui.DisplayDimensions;

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

        for (MenuBackgroundObject backgroundObject : backgroundAsteroids) {
            backgroundObject.update();

            boolean isInWidth = Math.abs(backgroundObject.getPosition().x) < MenuBackgroundManager.VIEWPORT_HEIGHT * displayDimensions.getRatio() * 0.8f;
            boolean isInHeight = Math.abs(backgroundObject.getPosition().y) < MenuBackgroundManager.VIEWPORT_HEIGHT * 0.8f;
            if (isInWidth && isInHeight) {
                retainedBackgroundAsteroids.add(backgroundObject);
            } else {
                retainedBackgroundAsteroids.add(buildAsteroid());
            }
        }
        backgroundAsteroids.clear();
        backgroundAsteroids.addAll(retainedBackgroundAsteroids);
    }

    public void draw(CommonDrawer uiDrawer) {
        for (MenuBackgroundObject backgroundObject : backgroundAsteroids) {
            backgroundObject.draw(uiDrawer);
        }
    }

    public MenuBackgroundObject buildAsteroid() {
        TextureAtlas.AtlasRegion texture = SolRandom.randomElement(availableAsteroidTextures);

        boolean small = SolRandom.test(.8f);
        float size = (small ? 1f : 2f) * SolRandom.randomFloat(.5f, 1);
        Color tint = new Color();
        SolColorUtil.fromHSB(SolRandom.randomFloat(0, 1), .25f, 1, .7f, tint);

        float ratio = displayDimensions.getRatio();
        float viewportHeight = MenuBackgroundManager.VIEWPORT_HEIGHT;
        Vector2 velocity, position;
        if (SolRandom.test(0.5f)) {
            // Spawn from the left or right of screen
            boolean fromLeft = SolRandom.test(0.5f);
            velocity = new Vector2((fromLeft ? 1 : -1) * SolRandom.randomFloat(0.75f, 2.5f), SolRandom.randomFloat(.75f, 2.5f));
            position = new Vector2((fromLeft ? -1 : 1) * viewportHeight * ratio * 0.8f, SolRandom.randomFloat(-viewportHeight * 0.8f, viewportHeight * 0.8f));
        } else {
            // Spawn from the top or bottom of screen
            boolean fromTop = SolRandom.test(0.5f);
            velocity = new Vector2(SolRandom.randomFloat(0.75f, 2.5f), (fromTop ? 1 : -1) * SolRandom.randomFloat(0.75f, 2.5f));
            position = new Vector2(SolRandom.randomFloat(-viewportHeight * ratio, viewportHeight * ratio), (fromTop ? -1 : 1) * viewportHeight * 0.8f);
        }

        //Give random rotation to asteroid
        float angle = SolRandom.randomFloat((float) Math.PI);
        float angularVelocity = SolRandom.randomFloat(1.5f);

        //Build the final asteroid body
        Body body = asteroidMeshLoader.getBodyAndSprite(world, texture, size, BodyDef.BodyType.DynamicBody, position, angle, new ArrayList<>(), 10f, DrawableLevel.BODIES);
        body.setLinearVelocity(velocity);
        body.setAngularVelocity(angularVelocity);
        MenuBackgroundObject asteroid = new MenuBackgroundObject(texture, size, tint, position, velocity, asteroidMeshLoader.getOrigin(texture.name, size).cpy(), angle, body);
        body.setUserData(asteroid);

        return asteroid;
    }
}
