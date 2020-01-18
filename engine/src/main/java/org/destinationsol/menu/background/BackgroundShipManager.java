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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.UiDrawer;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BackgroundShipManager {
    private DisplayDimensions displayDimensions;
    private CollisionMeshLoader shipMeshLoader;

    private List<String> nextShips;
    private List<BackgroundObject> backgroundShips;

    private World world;

    public BackgroundShipManager(DisplayDimensions displayDimensions, World world) {

        this.displayDimensions = displayDimensions;
        this.world = world;

        shipMeshLoader = new CollisionMeshLoader();

        nextShips = new ArrayList<String>() {
            //Add ships in the order they are supposed to be displayed
            {
                add("core:imperialTiny");
                add("core:imperialSmall");
                add("core:minerSmall");
                add("core:minerBoss");
                add("core:desertSmall");
                add("core:desertOrbiter");
            }
        };
        backgroundShips = new ArrayList<>();
    }


    public void addShip(String urnString) {
        TextureAtlas.AtlasRegion texture = Assets.getAtlasRegion(urnString);
        JSONObject rootNode = Validator.getValidatedJSON(urnString, "engine:schemaHullConfig");

        JSONObject rigidBodyNode = rootNode.getJSONObject("rigidBody");
        shipMeshLoader.readRigidBody(rigidBodyNode, urnString);
        float scale = 0.15f;
        float angle = 105f;
        Vector2 position = new Vector2(displayDimensions.getRatio() - displayDimensions.getRatio() / 8, -0.2f);
        Vector2 velocity = new Vector2(0.1f, 0);
        velocity.rotate(angle);
        Body body = shipMeshLoader.getBodyAndSprite(world, texture, scale * 0.9f, BodyDef.BodyType.DynamicBody, position, angle, new ArrayList<>(), Float.MAX_VALUE, DrawableLevel.BODIES);
        body.setLinearVelocity(velocity);
        Vector2 origin = shipMeshLoader.getOrigin(texture.name, scale);
        BackgroundObject ship = new BackgroundObject(texture, scale, SolColor.WHITE, position, velocity, origin.cpy(), angle, body);
        backgroundShips.add(ship);
    }

    public void update() {
        backgroundShips.forEach(ship -> ship.update());
        backgroundShips.removeIf(ship -> ship.getPosition().y >= 1.2);
        if (backgroundShips.isEmpty()) {
            //Spawn the next ship and put it at the last in the order
            String nextShipUrn = nextShips.get(0);
            nextShips.remove(0);
            nextShips.add(nextShipUrn);

            addShip(nextShipUrn);
        }
    }

    public void draw(UiDrawer uiDrawer) {
        backgroundShips.forEach(ship -> ship.draw(uiDrawer));
    }
}
