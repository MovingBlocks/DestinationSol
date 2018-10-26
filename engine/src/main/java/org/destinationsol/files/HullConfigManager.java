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
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolException;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.AbilityCommonConfigs;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.particle.DSParticleEmitter;
import org.destinationsol.game.ship.AbilityConfig;
import org.destinationsol.game.ship.EmWave;
import org.destinationsol.game.ship.KnockBack;
import org.destinationsol.game.ship.SloMo;
import org.destinationsol.game.ship.Teleport;
import org.destinationsol.game.ship.UnShield;
import org.destinationsol.game.ship.hulls.GunSlot;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HullConfigManager {
    private final ItemManager itemManager;
    private final AbilityCommonConfigs abilityCommonConfigs;
    private final Map<String, HullConfig> nameToConfigMap;
    private final Map<HullConfig, String> configToNameMap;

    public HullConfigManager(ItemManager itemManager, AbilityCommonConfigs abilityCommonConfigs) {
        this.itemManager = itemManager;
        this.abilityCommonConfigs = abilityCommonConfigs;

        nameToConfigMap = new HashMap<>();
        configToNameMap = new HashMap<>();
    }

    private static Vector2 readVector2(JsonValue jsonValue, String name, Vector2 defaultValue) {
        String string = jsonValue.getString(name, null);
        return (string == null)
                ? defaultValue
                : SolMath.readV2(string);
    }

    private static Engine.Config readEngineConfig(String engineName, ItemManager itemManager) {
        if (engineName == null)
            return null;

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

        Json json = Assets.getJson(shipName);

        try {
            readProperties(json.getJsonValue(), configData);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The JSON of ship " + shipName + " is missing, or has malformed, a required parameter" + e.getMessage().split(":")[1]);
        } catch (SerializationException e) {
            throw new SerializationException("The JSON of ship " + shipName + " has invalid syntax at " + e.getMessage().split(" near")[0].split("on ")[1]);
        }

        configData.tex = Assets.getAtlasRegion(shipName);
        configData.icon = Assets.getAtlasRegion(shipName + "Icon");

        validateEngineConfig(configData);

        json.dispose();

        return new HullConfig(configData);
    }

    private void parseGunSlotList(JsonValue containerNode, HullConfig.Data configData) {
        Vector2 builderOrigin = new Vector2(configData.shipBuilderOrigin);

        for (JsonValue gunSlotNode : containerNode) {
            Vector2 position = readVector2(gunSlotNode, "position", null);
            position.sub(builderOrigin).scl(configData.size);

            boolean isUnderneathHull = gunSlotNode.getBoolean("isUnderneathHull", false);
            boolean allowsRotation = gunSlotNode.getBoolean("allowsRotation", true);

            configData.gunSlots.add(new GunSlot(position, isUnderneathHull, allowsRotation));
        }
    }

    private void parseParticleEmitters(JsonValue containerNode, HullConfig.Data configData) {
        Vector2 builderOrigin = new Vector2(configData.shipBuilderOrigin);

        for (JsonValue particleEmitterNode : containerNode) {
            Vector2 position = readVector2(particleEmitterNode, "position", null);
            position.sub(builderOrigin).scl(configData.size);

            String trigger = particleEmitterNode.getString("trigger", null);
            float angleOffset = particleEmitterNode.getFloat("angleOffset", 0f);
            boolean hasLight = particleEmitterNode.getBoolean("hasLight", false);
            JsonValue particleNode = particleEmitterNode.get("particle");

            List<String> workSounds = new ArrayList<>();
            if (particleEmitterNode.hasChild("workSounds")) {
                workSounds = Arrays.asList(particleEmitterNode.get("workSounds").asStringArray());
            }

            configData.particleEmitters.add(new DSParticleEmitter(position, trigger, angleOffset, hasLight, particleNode, workSounds));
        }
    }

    private void readProperties(JsonValue rootNode, HullConfig.Data configData) {
        configData.size = rootNode.getFloat("size");
        configData.approxRadius = 0.4f * configData.size;
        configData.maxLife = rootNode.getInt("maxLife");

        configData.lightSrcPoss = SolMath.readV2List(rootNode, "lightSrcPoss");
        configData.hasBase = rootNode.getBoolean("hasBase", false);
        configData.forceBeaconPoss = SolMath.readV2List(rootNode, "forceBeaconPoss");
        configData.doorPoss = SolMath.readV2List(rootNode, "doorPoss");
        configData.type = HullConfig.Type.forName(rootNode.getString("type"));
        configData.durability = (configData.type == HullConfig.Type.BIG) ? 3 : .25f;
        configData.engineConfig = readEngineConfig(rootNode.getString("engine", null), itemManager);
        configData.ability = loadAbility(rootNode, itemManager, abilityCommonConfigs);

        configData.displayName = rootNode.getString("displayName", "---");
        configData.price = rootNode.getInt("price", 0);
        configData.hirePrice = rootNode.getFloat("hirePrice", 0);

        Vector2 tmpV;
        if (rootNode.has("rigidBody")) {
            tmpV = new Vector2(rootNode.get("rigidBody").get("origin").getFloat("x"),
                    1 - rootNode.get("rigidBody").get("origin").getFloat("y"));
        } else if (rootNode.has("rigidBodies")) {
            throw new SolException("Please rename 'rigidBodies' to 'rigidBody' in the JSON of ship " + configData.displayName);
        } else {
            throw new SolException("'rigidBody' not found in JSON of ship " + configData.displayName);
        }
        configData.shipBuilderOrigin.set(tmpV);

        process(configData);

        parseGunSlotList(rootNode.get("gunSlots"), configData);
        if (rootNode.has("particleEmitters")) {
            parseParticleEmitters(rootNode.get("particleEmitters"), configData);
        }
    }

    private AbilityConfig loadAbility(
            JsonValue hullNode,
            ItemManager manager,
            AbilityCommonConfigs commonConfigs
    ) {
        JsonValue abNode = hullNode.get("ability");
        if (abNode == null) {
            return null;
        }
        String type = abNode.getString("type");
        if ("sloMo".equals(type)) {
            return SloMo.Config.load(abNode, manager, commonConfigs.sloMo);
        }
        if ("teleport".equals(type)) {
            return Teleport.Config.load(abNode, manager, commonConfigs.teleport);
        }
        if ("knockBack".equals(type)) {
            return KnockBack.Config.load(abNode, manager, commonConfigs.knockBack);
        }
        if ("emWave".equals(type)) {
            return EmWave.Config.load(abNode, manager, commonConfigs.emWave);
        }
        if ("unShield".equals(type)) {
            return UnShield.Config.load(abNode, manager, commonConfigs.unShield);
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
