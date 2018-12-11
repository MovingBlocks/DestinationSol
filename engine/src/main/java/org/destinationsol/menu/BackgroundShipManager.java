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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.assets.Assets;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.UiDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BackgroundShipManager {
    private List<BackgroundShip> backgroundShips = new ArrayList<>();

    private MenuMeshLoader shipMeshLoader;

    private DisplayDimensions displayDimensions;

    private static final Logger logger = LoggerFactory.getLogger(BackgroundShipManager.class);

    public BackgroundShipManager(DisplayDimensions displayDimensions, World world) {

        this.displayDimensions = displayDimensions;
        shipMeshLoader = new MenuMeshLoader("engine:menuships", world);

        createPasserbyShip();
    }

    public void createPasserbyShip() {
        TextureAtlas.AtlasRegion texture = Assets.getAtlasRegion("engine:imperialTiny");
        float scale = 0.12f;
        float angle = 105f;
        Vector2 position = new Vector2(displayDimensions.getRatio() - displayDimensions.getRatio() / 8, -0.2f);
        Vector2 velocity = new Vector2(0.028f, 0);
        velocity.rotate(angle);
        Body body = shipMeshLoader.getBodyAndSprite(texture, scale * 0.9f, BodyDef.BodyType.DynamicBody, position, angle, new ArrayList<>(), Float.MAX_VALUE, DrawableLevel.BODIES);
        body.setLinearVelocity(velocity);
        Vector2 origin = shipMeshLoader.getOrigin("engine:imperialTiny", scale);
        BackgroundShip ship = new BackgroundShip(texture, scale, position, velocity, angle, origin.x, origin.y, body);
        backgroundShips.add(ship);
    }

    public void createHostileShip() {
        TextureAtlas.AtlasRegion texture = Assets.getAtlasRegion("engine:imperialSmall");
        float scale = 0.15f;
        float angle = 105f;
        Vector2 position = new Vector2(displayDimensions.getRatio() - displayDimensions.getRatio() / 8, -0.2f);
        Vector2 velocity = new Vector2(0.028f, 0);
        velocity.rotate(angle);
        Body body = shipMeshLoader.getBodyAndSprite(texture, scale * 0.9f, BodyDef.BodyType.DynamicBody, position, angle, new ArrayList<>(), Float.MAX_VALUE, DrawableLevel.BODIES);
        body.setLinearVelocity(velocity);
        Vector2 origin = shipMeshLoader.getOrigin("engine:imperialSmall", scale);
        BackgroundShip ship = new BackgroundShip(texture, scale, position, velocity, angle, origin.x, origin.y, body);
        backgroundShips.add(ship);
    }

    public void update() {
        backgroundShips.forEach(ship -> ship.update());
        backgroundShips.removeIf(ship -> ship.getPosition().y >= 1.2);
        if(backgroundShips.isEmpty()) {
            createHostileShip();
        }
    }

    public void draw(UiDrawer uiDrawer) {
        backgroundShips.forEach(ship -> ship.draw(uiDrawer));
    }
}
