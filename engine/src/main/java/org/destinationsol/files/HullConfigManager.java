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
package org.destinationsol.files;

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.game.AbilityCommonConfig;
import org.destinationsol.modules.ModuleManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.AbilityCommonConfigs;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.particle.DSParticleEmitter;
import org.destinationsol.game.ship.AbilityConfig;
import org.destinationsol.game.ship.hulls.GunSlot;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.terasology.module.Module;
import org.terasology.naming.Name;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class HullConfigManager {
    private final ItemManager itemManager;
    private final AbilityCommonConfigs abilityCommonConfigs;
    private final Map<String, HullConfig> nameToConfigMap;
    private final Map<HullConfig, String> configToNameMap;
    private static final Map<String, Class<AbilityConfig>> abilityClasses;
    private static final String LOAD_JSON_METHOD_NAME = "load";

    static {
        abilityClasses = new HashMap<String, Class<AbilityConfig>>();
        for (Class abilityClass : ModuleManager.getEnvironment().getSubtypesOf(AbilityConfig.class)) {
            try {
                abilityClasses.put(abilityClass.getSimpleName().replace("Config", "").toLowerCase(Locale.ENGLISH), abilityClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public HullConfigManager(ItemManager itemManager, AbilityCommonConfigs abilityCommonConfigs) {
        this.itemManager = itemManager;
        this.abilityCommonConfigs = abilityCommonConfigs;

        nameToConfigMap = new HashMap<>();
        configToNameMap = new HashMap<>();
    }

    private static Vector2 readVector2(JSONObject JSONObject, String name, Vector2 defaultValue) {
        String string = JSONObject.optString(name, null);
        return (string == null)
                ? defaultValue
                : SolMath.readV2(string);
    }

    private static Engine.Config readEngineConfig(String engineName, ItemManager itemManager) {
        if (engineName == null) {
            return null;
        }

        return itemManager.getEngineConfig(engineName);
    }

    private static void validateEngineConfig(HullConfig.Data hull) {
        if (hull.engineConfig != null) {
            // Stations can't have engines, and the engine size must match the hull size
            if (hull.type == HullConfig.Type.STATION || hull.engineConfig.isBig != (hull.type == HullConfig.Type.BIG)) {
                throw new AssertionError("Incompatible engine in hull " + hull.displayName);
            }
        }
    }

    public HullConfig getConfig(String shipName) {
        HullConfig hullConfig = nameToConfigMap.get(shipName);

        if (hullConfig == null) {
            hullConfig = read(shipName);

            nameToConfigMap.put(shipName, hullConfig);
            configToNameMap.put(hullConfig, shipName);
        }

        return hullConfig;
    }

    public String getName(HullConfig hull) {
        String name = configToNameMap.get(hull);
        return (name == null) ? "" : name;
    }

    private HullConfig read(String shipName) {
        final HullConfig.Data configData = new HullConfig.Data();

        configData.internalName = shipName;

        JSONObject rootNode = Validator.getValidatedJSON(shipName, "engine:schemaHullConfig");

        readProperties(rootNode, configData);

        configData.tex = Assets.getAtlasRegion(shipName);
        configData.icon = Assets.getAtlasRegion(shipName + "Icon");

        validateEngineConfig(configData);

        return new HullConfig(configData);
    }

    private void parseGunSlotList(JSONArray containerNode, HullConfig.Data configData) {
        Vector2 builderOrigin = new Vector2(configData.shipBuilderOrigin);

        for (int i = 0; i < containerNode.length(); i++) {
            JSONObject gunSlotNode = containerNode.getJSONObject(i);
            Vector2 position = readVector2(gunSlotNode, "position", null);
            position.sub(builderOrigin).scl(configData.size);

            boolean isUnderneathHull = gunSlotNode.optBoolean("isUnderneathHull", false);
            boolean allowsRotation = gunSlotNode.optBoolean("allowsRotation", true);

            configData.gunSlots.add(new GunSlot(position, isUnderneathHull, allowsRotation));
        }
    }

    private void parseParticleEmitters(JSONArray containerNode, HullConfig.Data configData) {
        Vector2 builderOrigin = new Vector2(configData.shipBuilderOrigin);

        for (int i = 0; i < containerNode.length(); i++) {
            JSONObject particleEmitterNode = containerNode.getJSONObject(i);
            Vector2 position = readVector2(particleEmitterNode, "position", null);
            position.sub(builderOrigin).scl(configData.size);

            String trigger = particleEmitterNode.optString("trigger", null);
            float angleOffset = (float) particleEmitterNode.optDouble("angleOffset", 0f);
            boolean hasLight = particleEmitterNode.optBoolean("hasLight", false);
            JSONObject particleNode = particleEmitterNode.getJSONObject("particle");

            List<String> workSounds = new ArrayList<>();
            if (particleEmitterNode.has("workSounds")) {
                workSounds = Assets.convertToStringList(particleEmitterNode.getJSONArray("workSounds"));
            }

            configData.particleEmitters.add(new DSParticleEmitter(position, trigger, angleOffset, hasLight, particleNode, workSounds));
        }
    }

    private void readProperties(JSONObject rootNode, HullConfig.Data configData) {
        configData.size = (float) rootNode.optDouble("size");
        configData.approxRadius = 0.4f * configData.size;
        configData.maxLife = rootNode.getInt("maxLife");

        configData.lightSrcPoss = SolMath.readV2List(rootNode, "lightSrcPoss");
        configData.hasBase = rootNode.optBoolean("hasBase");
        configData.forceBeaconPoss = SolMath.readV2List(rootNode, "forceBeaconPoss");
        configData.doorPoss = SolMath.readV2List(rootNode, "doorPoss");
        configData.type = HullConfig.Type.forName(rootNode.optString("type"));
        configData.durability = (configData.type == HullConfig.Type.BIG) ? 3 : .25f;
        configData.engineConfig = readEngineConfig(rootNode.optString("engine", null), itemManager);
        configData.ability = loadAbility(rootNode, itemManager, abilityCommonConfigs);

        configData.displayName = rootNode.optString("displayName", "---");
        configData.price = rootNode.optInt("price", 0);
        configData.hirePrice = (float) rootNode.optDouble("hirePrice", 0);

        Vector2 tmpV = new Vector2((float) rootNode.getJSONObject("rigidBody").getJSONObject("origin").getDouble("x"),
                1 - (float) rootNode.getJSONObject("rigidBody").getJSONObject("origin").getDouble("y"));
        configData.shipBuilderOrigin.set(tmpV);

        process(configData);

        parseGunSlotList(rootNode.getJSONArray("gunSlots"), configData);
        if (rootNode.has("particleEmitters")) {
            parseParticleEmitters(rootNode.getJSONArray("particleEmitters"), configData);
        }
    }

    private AbilityConfig loadAbility(
            JSONObject hullNode,
            ItemManager manager,
            AbilityCommonConfigs commonConfigs
    ) {
        JSONObject abNode = hullNode.has("ability") ? hullNode.getJSONObject("ability") : null;
        if (abNode == null) {
            return null;
        }
        String type = abNode.optString("type").toLowerCase(Locale.ENGLISH);

        if (abilityClasses.containsKey(type)) {
            try {
                Method loadMethod = abilityClasses.get(type).getDeclaredMethod(LOAD_JSON_METHOD_NAME, JSONObject.class, ItemManager.class, AbilityCommonConfig.class);
                return (AbilityConfig) loadMethod.invoke(null, abNode, manager, commonConfigs.abilityConfigs.get(type));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    // Seems to offsets all positions by the shipbuilder origin
    // Todo: Find out what this function does and provide a better name.
    private void process(HullConfig.Data configData) {
        Vector2 builderOrigin = new Vector2(configData.shipBuilderOrigin);

        configData.origin.set(builderOrigin).scl(configData.size);

        for (Vector2 position : configData.lightSrcPoss) {
            position.sub(builderOrigin).scl(configData.size);
        }

        for (Vector2 position : configData.forceBeaconPoss) {
            position.sub(builderOrigin).scl(configData.size);
        }

        for (Vector2 position : configData.doorPoss) {
            position.sub(builderOrigin).scl(configData.size);
        }
    }
}
