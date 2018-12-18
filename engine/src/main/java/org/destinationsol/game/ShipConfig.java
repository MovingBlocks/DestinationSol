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
package org.destinationsol.game;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.json.Validator;
import org.json.JSONArray;
import org.json.JSONObject;
import com.badlogic.gdx.utils.SerializationException;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.Set;

public class ShipConfig {
    public final HullConfig hull;
    public final String items;
    public final int money;
    public final float density;
    public final ShipConfig guard;
    public final float dps;
    public Vector2 spawnPos;

    public ShipConfig(HullConfig hull, String items, int money, float density, ShipConfig guard, ItemManager itemManager) {
        this.hull = hull;
        this.items = items;
        this.money = money;
        this.density = density;
        this.guard = guard;
        dps = HardnessCalc.getShipConfDps(this, itemManager);
    }

    public ShipConfig(HullConfig hull, String items, int money, float density, ShipConfig guard, ItemManager itemManager, Vector2 spawnPos) {
        this(hull, items, money, density, guard, itemManager);
        this.spawnPos = spawnPos;
    }

    public Vector2 getSpawnPos() {
        return spawnPos;
    }

    public int getMoney(){
        return money;
    }

    public HullConfig getHull(){
        return hull;
    }

    public String getItems(){
        return items;
    }

    public static ArrayList<ShipConfig> loadList(JSONArray shipListJson, HullConfigManager hullConfigs, ItemManager itemManager) {
        ArrayList<ShipConfig> res = new ArrayList<>();
        if (shipListJson == null) {
            return res;
        }
        for (int i = 0; i < shipListJson.length(); i++) {
            JSONObject shipNode = shipListJson.getJSONObject(i);
            ShipConfig c = load(hullConfigs, shipNode, itemManager);
            res.add(c);
        }
        return res;
    }

    public static ShipConfig load(HullConfigManager hullConfigs, String shipName, ItemManager itemManager) {
        Set<ResourceUrn> configUrnList = Assets.getAssetHelper().list(Json.class, "[a-zA-Z]*:playerSpawnConfig");

        ShipConfig shipConfig = null;

        for (ResourceUrn configUrn : configUrnList) {
            JSONObject rootNode = Validator.getValidatedJSON(configUrn.toString(), "engine:schemaPlayerSpawnConfig");

            if (rootNode.keySet().contains(shipName) && rootNode.get(shipName) instanceof JSONObject) {
                shipConfig = load(hullConfigs, rootNode.has(shipName) ? rootNode.getJSONObject(shipName) : null, itemManager);
            }

            if (shipConfig != null) {
                break;
            }
        }

        return shipConfig;
    }

    public static ShipConfig load(HullConfigManager hullConfigs, JSONObject rootNode, ItemManager itemManager) {
        if (rootNode == null) {
            return null;
        }

        String hullName = rootNode.getString("hull");
        HullConfig hull = hullConfigs.getConfig(hullName);

        ShipConfig guard;
        if (rootNode.has("guard")) {
            guard = load(hullConfigs, rootNode.getJSONObject("guard"), itemManager);
        } else {
            guard = null;
        }

        String items = rootNode.getString("items");
        int money = rootNode.optInt("money", 0);
        float density = (float) rootNode.optDouble("density", -1);

        return new ShipConfig(hull, items, money, density, guard, itemManager);
    }
}
