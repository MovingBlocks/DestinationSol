/*
 * Copyright 2015 MovingBlocks
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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.AbilityCommonConfigs;
import org.destinationsol.game.item.EngineItem;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.ship.AbilityConfig;
import org.destinationsol.game.ship.EmWave;
import org.destinationsol.game.ship.KnockBack;
import org.destinationsol.game.ship.ShipBuilder;
import org.destinationsol.game.ship.SloMo;
import org.destinationsol.game.ship.Teleport;
import org.destinationsol.game.ship.UnShield;
import org.destinationsol.game.ship.hulls.GunSlot;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class HullConfigManager {

    public static final String PROPERTIES_FILE_NAME = "properties.json";
    public static final String TEXTURE_FILE_NAME = "texture.png";
    public static final String ICON_FILE_NAME = "icon.png";
    private final FileManager fileManager;
    private final TextureManager textureManager;
    private final ItemManager itemManager;
    private final AbilityCommonConfigs abilityCommonConfigs;
    private final Map<String, HullConfig> nameToConfigMap;
    private final Map<HullConfig, String> configToNameMap;

    public HullConfigManager(ShipBuilder shipBuilder,
                             FileManager fileManager,
                             TextureManager textureManager,
                             ItemManager itemManager,
                             AbilityCommonConfigs abilityCommonConfigs
    ) {
        this.fileManager = fileManager;
        this.textureManager = textureManager;
        this.itemManager = itemManager;
        this.abilityCommonConfigs = abilityCommonConfigs;

        nameToConfigMap = new HashMap<>();
        configToNameMap = new HashMap<>();
        readHullConfigs();
    }

    private static Vector2 readVector2(JsonValue jsonValue, String name, Vector2 defaultValue) {
        String string = jsonValue.getString(name, null);
        return (string == null)
                ? defaultValue
                : SolMath.readV2(string);
    }

    private static EngineItem.Config readEngineConfig(ItemManager itemManager, JsonValue jsonValue, String name) {
        String string = jsonValue.getString(name, null);
        return itemManager.getEngineConfigs().get(string);
    }

    private static void validateEngineConfig(HullConfig.Data hull) {
        if (hull.engineConfig != null) {
            if (    // stations can't have engines
                    (hull.type == HullConfig.Type.STATION) ||
                    // the engine size must match the hull size
                    (hull.engineConfig.big != (hull.type == HullConfig.Type.BIG))
                    ) {
                throw new AssertionError("incompatible engine in hull " + hull.displayName);
            }
        }
    }

    public HullConfig getConfig(String name) {
        return nameToConfigMap.get(name);
    }

    public String getName(HullConfig hull) {
        String result = configToNameMap.get(hull);
        return (result == null) ? "" : result;
    }

    private void readHullConfigs() {
        List<FileHandle> hullDirectories = getHullDirectories();

        for (FileHandle handle : hullDirectories) {
            HullConfig config = read(handle);
            String name = handle.nameWithoutExtension();
            nameToConfigMap.put(name, config);
            configToNameMap.put(config, name);
        }
    }

    private List<FileHandle> getHullDirectories() {
        List<FileHandle> subDirectories = new LinkedList<FileHandle>();

        for (FileHandle handle : fileManager.getHullsDirectory().list()) {
            if (handle.isDirectory()) {
                subDirectories.add(handle);
            }
        }

        return subDirectories;
    }

    private HullConfig read(FileHandle hullConfigDirectory) {
        final HullConfig.Data configData = new HullConfig.Data();

        final FileHandle propertiesFile = hullConfigDirectory.child(PROPERTIES_FILE_NAME);
        readProperties(propertiesFile, configData);

        String internalName = hullConfigDirectory.nameWithoutExtension();

        configData.internalName = internalName;
        configData.tex = textureManager.getTexture(hullConfigDirectory.child(TEXTURE_FILE_NAME).toString());
        configData.icon = textureManager.getTexture(hullConfigDirectory.child(ICON_FILE_NAME).toString());

        validateEngineConfig(configData);

        return new HullConfig(configData);
    }

    private void parseGunSlotList(JsonValue containerNode, HullConfig.Data configData) {
        Vector2 builderOrigin = new Vector2(configData.shipBuilderOrigin);

        for (JsonValue gunSlotNode : containerNode) {
            Vector2 position = readVector2(gunSlotNode, "position", null);
            position.sub(builderOrigin)
                    .scl(configData.size);

            boolean isUnderneathHull = gunSlotNode.getBoolean("isUnderneathHull", false);
            boolean allowsRotation = gunSlotNode.getBoolean("allowsRotation", true);

            configData.gunSlots.add(new GunSlot(position, isUnderneathHull, allowsRotation));
        }
    }

    private void readProperties(FileHandle propertiesFile, HullConfig.Data configData) {
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonNode = jsonReader.parse(propertiesFile);

        configData.size = jsonNode.getFloat("size");
        configData.approxRadius = 0.4f * configData.size;
        configData.maxLife = jsonNode.getInt("maxLife");

        configData.e1Pos = readVector2(jsonNode, "e1Pos", new Vector2());
        configData.e2Pos = readVector2(jsonNode, "e2Pos", new Vector2());

        configData.lightSrcPoss = SolMath.readV2List(jsonNode, "lightSrcPoss");
        configData.hasBase = jsonNode.getBoolean("hasBase", false);
        configData.forceBeaconPoss = SolMath.readV2List(jsonNode, "forceBeaconPoss");
        configData.doorPoss = SolMath.readV2List(jsonNode, "doorPoss");
        configData.type = HullConfig.Type.forName(jsonNode.getString("type"));
        configData.durability = (configData.type == HullConfig.Type.BIG) ? 3 : .25f;
        configData.engineConfig = readEngineConfig(itemManager, jsonNode, "engine");
        configData.ability = loadAbility(jsonNode, itemManager, abilityCommonConfigs);

        configData.displayName = jsonNode.getString("displayName", "---");
        configData.price = jsonNode.getInt("price", 0);
        configData.hirePrice = jsonNode.getFloat("hirePrice", 0);

        Vector2 tmpV = new Vector2(jsonNode.get("rigidBody").get("origin").getFloat("x"),
                1 - jsonNode.get("rigidBody").get("origin").getFloat("y"));
        configData.shipBuilderOrigin.set(tmpV);

        process(configData);

        parseGunSlotList(jsonNode.get("gunSlots"), configData);
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

        configData.origin.set(builderOrigin)
                .scl(configData.size);

        configData.e1Pos.sub(builderOrigin)
                .scl(configData.size);

        configData.e2Pos.sub(builderOrigin)
                .scl(configData.size);

        for (Vector2 position : configData.lightSrcPoss) {
            position.sub(builderOrigin)
                    .scl(configData.size);
        }

        for (Vector2 position : configData.forceBeaconPoss) {
            position.sub(builderOrigin)
                    .scl(configData.size);
        }

        for (Vector2 position : configData.doorPoss) {
            position.sub(builderOrigin)
                    .scl(configData.size);
        }
    }
}
