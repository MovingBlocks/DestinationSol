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
import org.destinationsol.CommonDrawer;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.CollisionMeshLoader;
import org.destinationsol.game.drawables.DrawableLevel;
import org.destinationsol.ui.DisplayDimensions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.terasology.gestalt.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MenuBackgroundShipManager {
    private DisplayDimensions displayDimensions;
    private CollisionMeshLoader shipMeshLoader;

    private List<String> availableShips;
    private List<MenuBackgroundObject> backgroundShips;

    private World world;

    public MenuBackgroundShipManager(DisplayDimensions displayDimensions, World world) {

        this.displayDimensions = displayDimensions;
        this.world = world;

        shipMeshLoader = new CollisionMeshLoader();

        availableShips = new ArrayList<>();
        backgroundShips = new ArrayList<>();

        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().list(Json.class, "[a-zA-Z]*:menuBackgroundShipConfig");
        for (ResourceUrn configUrn : configUrnList) {
            String moduleName = configUrn.toString().split(":")[0];
            JSONObject rootNode = Assets.getJson(configUrn.toString()).getJsonValue();
            JSONArray ships = rootNode.getJSONArray("Menu Ships");
            ships.forEach(ship -> availableShips.add(moduleName + ":" + ship));
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

    public void draw(CommonDrawer uiDrawer) {
        backgroundShips.forEach(ship -> ship.draw(uiDrawer));
    }
}
