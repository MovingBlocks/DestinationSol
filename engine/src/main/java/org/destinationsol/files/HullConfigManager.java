/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.common.SolDescriptiveException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.AbilityCommonConfigs;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.particle.DSParticleEmitter;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.ship.AbilityConfig;
import org.destinationsol.game.ship.EmWave;
import org.destinationsol.game.ship.KnockBack;
import org.destinationsol.game.ship.SloMo;
import org.destinationsol.game.ship.Teleport;
import org.destinationsol.game.ship.UnShield;
import org.destinationsol.game.ship.hulls.GunSlot;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    private static Vector2 readOptionalVector2(JsonValue jsonValue, @Nullable String name, @Nullable Vector2 defaultValue) {
        Optional<String> string = Optional.ofNullable(jsonValue.getString(name, null));

        return string.map(SolMath::readVector2String).orElse(defaultValue);
    }

    /**
     * Reads and parses a position field from given {@link JsonValue}.
     *
     * The position field is expected to be of type string, in the form of {@code "0.5 0.5"}. This would return a {@link Vector2} with values (0.5f, 0.5f).
     *
     * @param jsonValue Json from which you want to get the position
     * @param superComponent Descriptive name of parameter whose position you are trying to get. Will be used as part of exception message in case the position is not found in the Json
     * @return {@code Vector2} representation of the position
     */
    private static @NotNull Vector2 readPosition(@NotNull JsonValue jsonValue, @Nullable String superComponent) {
        Optional<String> string = Optional.ofNullable(jsonValue.getString("position", null));

        return string
                .map(SolMath::readVector2String)
                .orElseThrow(() -> new SolDescriptiveException("Some of your " + superComponent + "s is missing a required \"position\" argument, or it is malformed."));
    }


    private static @Nullable Engine.Config readEngineConfig(@Nullable String engineName, @NotNull ItemManager itemManager) {
        Optional<String> nameOptional = Optional.ofNullable(engineName);

        return nameOptional.map(itemManager::getEngineConfig).orElse(null);
    }

    private static void validateEngineConfig(@NotNull HullConfig.Data hull) {
        if (hull.engineConfig != null) {
            // Stations can't have engines, and the engine size must match the hull size
            if (hull.type == HullConfig.Type.STATION || hull.engineConfig.big != (hull.type == HullConfig.Type.BIG)) {
                throw new AssertionError("Incompatible engine in hull " + hull.displayName);
            }
        }
    }

    public @NotNull HullConfig getConfig(@NotNull String shipName) {
        HullConfig hullConfig = nameToConfigMap.get(shipName);

        if (hullConfig == null) {
            hullConfig = read(shipName);

            nameToConfigMap.put(shipName, hullConfig);
            configToNameMap.put(hullConfig, shipName);
        }

        return hullConfig;
    }

    public @NotNull String getName(@Nullable HullConfig hull) {
        Optional<String> name = Optional.ofNullable(configToNameMap.get(hull));
        return name.orElse("");
    }

    private @NotNull HullConfig read(@NotNull String shipName) {
        final HullConfig.Data configData = new HullConfig.Data();

        configData.internalName = shipName;

        Json json = Assets.getJson(shipName);

        readProperties(json.getJsonValue(), configData);

        configData.tex = Assets.getAtlasRegion(shipName);
        configData.icon = Assets.getAtlasRegion(shipName + "Icon");

        validateEngineConfig(configData);

        json.dispose();

        return new HullConfig(configData);
    }

    private void parseGunSlotList(@NotNull JsonValue containerNode, @NotNull HullConfig.Data configData) {
        Vector2 builderOrigin = new Vector2(configData.shipBuilderOrigin);

        for (JsonValue gunSlotNode : containerNode) {
            Vector2 position = readPosition(gunSlotNode,"gunSlot");
            position.sub(builderOrigin).scl(configData.size);

            boolean isUnderneathHull = gunSlotNode.getBoolean("isUnderneathHull", false);
            boolean allowsRotation = gunSlotNode.getBoolean("allowsRotation", true);

            configData.gunSlots.add(new GunSlot(position, isUnderneathHull, allowsRotation));
        }
    }

    private void parseParticleEmitterSlots(@NotNull JsonValue containerNode, @NotNull HullConfig.Data configData) {
        Vector2 builderOrigin = new Vector2(configData.shipBuilderOrigin);

        for (JsonValue particleEmitterSlotNode : containerNode) {
            Vector2 position = readPosition(particleEmitterSlotNode,"particleEmitter");
            position.sub(builderOrigin).scl(configData.size);

            String trigger = particleEmitterSlotNode.getString("trigger", null);
            float angleOffset = particleEmitterSlotNode.getFloat("angleOffset", 0f);
            boolean hasLight = particleEmitterSlotNode.getBoolean("hasLight", false);
            JsonValue particleNode = particleEmitterSlotNode.get("particle");
            EffectConfig effectConfig = EffectConfig.load(particleNode, new EffectTypes(), new GameColors());

            configData.particleEmitters.add(new DSParticleEmitter(position, trigger, angleOffset, hasLight, effectConfig));
        }
    }

    private void readProperties(@NotNull JsonValue rootNode, @NotNull HullConfig.Data configData) {
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

        Vector2 tmpV = new Vector2(rootNode.get("rigidBody").get("origin").getFloat("x"),
                1 - rootNode.get("rigidBody").get("origin").getFloat("y"));
        configData.shipBuilderOrigin.set(tmpV);

        process(configData);

        parseGunSlotList(rootNode.get("gunSlots"), configData);
        if (rootNode.has("particleEmitters")) {
            parseParticleEmitterSlots(rootNode.get("particleEmitters"), configData);
        }
    }

    private @Nullable AbilityConfig loadAbility(
            @NotNull JsonValue hullNode,
            @NotNull ItemManager manager,
            @NotNull AbilityCommonConfigs commonConfigs
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
    private void process(@NotNull HullConfig.Data configData) {
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
