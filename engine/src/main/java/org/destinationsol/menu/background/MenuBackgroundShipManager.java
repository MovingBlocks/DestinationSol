/*
 * Copyright 2020 MovingBlocks
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
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.UiDrawer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.terasology.gestalt.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <h1>Manages background ships for the menu</h1>
 * Spawns and keeps track of Ships (non-controllable) in the form of {@link org.destinationsol.menu.background.MenuBackgroundObject},
 * which are spawned at the top right corner of the screen and move down gradually to the bottom left corner.
 * These ships can be added by modules by using a 'menuBackgroundShipConfig.json' config file
 */
public class MenuBackgroundShipManager {
    private CollisionMeshLoader shipMeshLoader;

    private List<String> availableShips;
    private List<MenuBackgroundObject> backgroundShips;

    private World world;

    public MenuBackgroundShipManager(DisplayDimensions displayDimensions, World world) {
        this.world = world;

        shipMeshLoader = new CollisionMeshLoader();

        availableShips = new ArrayList<>();
        backgroundShips = new ArrayList<>();

        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().listAssets(Json.class, "menuBackgroundShipConfig");
        for (ResourceUrn configUrn : configUrnList) {
            JSONObject rootNode = Assets.getJson(configUrn.toString()).getJsonValue();
            JSONArray ships = rootNode.getJSONArray("Menu Ships");
            // JSONArray.iterator must not be used (foreach uses it internally), as Android does not support it
            // (you cannot override the dependency either, as it is a system library).
            for (int index = 0; index < ships.length(); index++) {
                Object shipUrn = ships.get(index);
                if (!availableShips.contains(shipUrn.toString())) {
                    availableShips.add(shipUrn.toString());
                }
            }
        }
    }


    public void addShip(String urnString) {
        TextureAtlas.AtlasRegion texture = Assets.getAtlasRegion(urnString);
        JSONObject rootNode = Validator.getValidatedJSON(urnString, "engine:schemaHullConfig");

        JSONObject rigidBodyNode = rootNode.getJSONObject("rigidBody");
        shipMeshLoader.readRigidBody(rigidBodyNode, urnString);
        float scale = 1f;
        float angle = 105f;
        Vector2 position = new Vector2(4f, -4f);
        Vector2 velocity = new Vector2(.5f, 0);
        velocity.rotate(angle);
        Body body = shipMeshLoader.getBodyAndSprite(world, texture, scale, BodyDef.BodyType.DynamicBody, position, angle, new ArrayList<>(), Float.MAX_VALUE, DrawableLevel.BODIES);
        body.setLinearVelocity(velocity);
        Vector2 origin = shipMeshLoader.getOrigin(texture.name, scale);
        MenuBackgroundObject ship = new MenuBackgroundObject(texture, scale, SolColor.WHITE, position, velocity, origin.cpy(), angle, body);
        backgroundShips.add(ship);
    }

    public void update() {
        backgroundShips.forEach(ship -> ship.update());
        backgroundShips.removeIf(ship -> ship.getPosition().y >= 3f);
        if (backgroundShips.isEmpty()) {
            //Spawn a random ship
            String nextShipUrn = SolRandom.randomElement(availableShips);
            addShip(nextShipUrn);
        }
    }

    public void draw(UiDrawer uiDrawer) {
        backgroundShips.forEach(ship -> ship.draw(uiDrawer));
    }
}
