package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.miloshpetrov.sol2.*;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.files.FileManager;
import com.miloshpetrov.sol2.game.AbilityCommonConfigs;
import com.miloshpetrov.sol2.game.item.EngineItem;
import com.miloshpetrov.sol2.game.item.ItemManager;
import com.miloshpetrov.sol2.game.sound.SoundManager;

import java.util.*;

public class HullConfigs {
    private final HashMap<String,HullConfig> hullConfigs;

    private static JsonValue getHullFileParentNode(FileHandle configFile) {
        final JsonReader jsonReader = new JsonReader();
        return jsonReader.parse(configFile);
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
                    ( hull.type == HullConfig.Type.STATION ) ||
                    // the engine size must match the hull size
                    ( hull.engineConfig.big != (hull.type == HullConfig.Type.BIG) )
            ) {
                throw new AssertionError("incompatible engine in hull " + hull.displayName);
            }
        }
    }

    public HullConfigs(ShipBuilder shipBuilder,
                       TextureManager textureManager,
                       ItemManager itemManager,
                       AbilityCommonConfigs abilityCommonConfigs,
                       SoundManager soundManager)
    {
        hullConfigs = new HashMap<String, HullConfig>();
        final FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("hulls.json");
        final JsonValue parentNode = getHullFileParentNode(configFile);

        for (JsonValue hullNode : parentNode ) {
            HullConfig.Data configData = new HullConfig.Data();
            configData.textureName = hullNode.getString("texName");
            configData.size = hullNode.getFloat("size");
            configData.maxLife = hullNode.getInt("maxLife");
            configData.e1Pos = readVector2(hullNode, "e1Pos", new Vector2());
            configData.e2Pos = readVector2(hullNode, "e2Pos", new Vector2());
            configData.g1Pos = readVector2(hullNode, "g1Pos", null);
            configData.g2Pos = readVector2(hullNode, "g2Pos", null);
            configData.lightSrcPoss = SolMath.readV2List(hullNode, "lightSrcPoss");
            configData.hasBase = hullNode.getBoolean("hasBase", false);
            configData.forceBeaconPoss = SolMath.readV2List(hullNode, "forceBeaconPoss");
            configData.doorPoss = SolMath.readV2List(hullNode, "doorPoss");
            configData.type = HullConfig.Type.forName(hullNode.getString("type"));
            configData.durability = (configData.type == HullConfig.Type.BIG) ? 3 : .25f;
            configData.tex = textureManager.getTex("hulls/" + configData.textureName, configFile);
            configData.icon = textureManager.getTex(TextureManager.HULL_ICONS_DIR + configData.textureName, configFile);
            configData.engineConfig = readEngineConfig(itemManager, hullNode, "engine");
            configData.ability = loadAbility(hullNode, itemManager, abilityCommonConfigs, soundManager);
            configData.g1UnderShip = hullNode.getBoolean("g1UnderShip", false);
            configData.g2UnderShip = hullNode.getBoolean("g2UnderShip", false);
            configData.m1Fixed = hullNode.getBoolean("m1Fixed", false);
            configData.m2Fixed = hullNode.getBoolean("m2Fixed", false);
            configData.displayName = hullNode.getString("displayName", "---");
            configData.price = hullNode.getInt("price", 0);
            configData.hirePrice = hullNode.getFloat("hirePrice", 0);

            validateEngineConfig(configData);
            process(configData, shipBuilder);

            hullConfigs.put(hullNode.name, new HullConfig(configData));
        }
    }

    private AbilityConfig loadAbility(
            JsonValue hullNode,
            ItemManager itemManager,
            AbilityCommonConfigs abilityCommonConfigs,
            SoundManager soundManager)
    {
        JsonValue abNode = hullNode.get("ability");
        if (abNode == null) return null;
        String type = abNode.getString("type");
        if ("sloMo".equals(type)) return SloMo.Config.load(abNode, itemManager, abilityCommonConfigs.sloMo);
        if ("teleport".equals(type)) return Teleport.Config.load(abNode, itemManager, abilityCommonConfigs.teleport);
        if ("knockBack".equals(type)) return KnockBack.Config.load(abNode, itemManager, abilityCommonConfigs.knockBack);
        if ("emWave".equals(type)) return EmWave.Config.load(abNode, itemManager, abilityCommonConfigs.emWave);
        if ("unShield".equals(type)) return UnShield.Config.load(abNode, itemManager, abilityCommonConfigs.unShield);
        return null;
    }

    public HullConfig getConfig(String name) {
        return hullConfigs.get(name);
    }

    // Seems to offsets all positions by the shipbuilder origin
    // Todo: Find out what this function does and provide a better name.
    private void process(HullConfig.Data configData, ShipBuilder shipBuilder) {
        Vector2 builderOrigin = shipBuilder.getOrigin(configData.textureName);

        configData.origin.set(builderOrigin)
                         .scl(configData.size);

        configData.g1Pos.sub(builderOrigin)
                        .scl(configData.size);

        if (configData.g2Pos != null) {
            configData.g2Pos.sub(builderOrigin)
                            .scl(configData.size);
        }

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

    public String getName(HullConfig hull) {
        for (Map.Entry<String, HullConfig> e : hullConfigs.entrySet()) {
            if (hull == e.getValue()) {
                return e.getKey();
            }
        }
        return "";
    }
}
