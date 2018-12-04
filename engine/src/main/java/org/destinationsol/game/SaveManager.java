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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.destinationsol.IniReader;
import org.destinationsol.common.SolRandom;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.item.ItemContainer;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    protected static final String SAVE_FILE_NAME = "prevShip.ini";
    protected static final String MERC_SAVE_FILE = "mercenaries.json";
    protected static final String WORLD_SAVE_FILE_NAME = "world.json";

    private static Logger logger = LoggerFactory.getLogger(SaveManager.class);

    protected SaveManager() { }

    public static void writeShips(HullConfig hull, float money, List<SolItem> itemsList, Hero hero, HullConfigManager hullConfigManager) {
        String hullName = hullConfigManager.getName(hull);

        writeMercs(hero, hullConfigManager);

        String items = itemsToString(itemsList);

        Vector2 pos = hero.getPosition();

        IniReader.write(SAVE_FILE_NAME, "hull", hullName, "money", (int) money, "items", items, "x", pos.x, "y", pos.y);
    }

    /**
     * Encodes the given list of SolItems as a string.
     *
     * @param items A list of SolItems to be encoded as a string
     * @return A string of items suitable for saving
     */
    private static String itemsToString(List<SolItem> items) {
        StringBuilder sb = new StringBuilder();

        for (SolItem i : items) {
            sb.append(i.getCode());
            if (i.isEquipped() > 0) {
                sb.append("-").append(i.isEquipped());
            }
            sb.append(" ");
            // Save gun's loaded ammo
            if (i instanceof Gun) {
                Gun g = (Gun) i;
                if ((g.ammo > 0 || g.reloadAwait > 0) && !g.config.clipConf.infinite) {
                    sb.append(g.config.clipConf.code).append(" ");
                }
            }
        }

        return sb.toString();
    }

    /**
     * Writes the player's mercenaries to their JSON file.
     * The file will be created if it doesn't exist.
     *
     * @param hero The hero we're dealing with
     */
    private static void writeMercs(Hero hero, HullConfigManager hullConfigManager) {
        PrintWriter writer;

        ItemContainer mercenaries = hero.getMercs();

        List<JsonObject> jsons = new ArrayList<JsonObject>();

        for (List<SolItem> group : mercenaries) {
            for (SolItem item : group) {
                SolShip merc = ((MercItem) item).getSolShip();
                // Json fields
                String hullName = hullConfigManager.getName(merc.getHull().config);
                int money = (int) merc.getMoney();

                ArrayList<SolItem> itemsList = new ArrayList<>();
                for (List<SolItem> itemGroup : merc.getItemContainer()) {
                    for (SolItem itemInGroup : itemGroup) {
                        itemsList.add(0, itemInGroup);
                    }
                }
                String items = itemsToString(itemsList);

                JsonObject json = new JsonObject();
                json.addProperty("hull", hullName);
                json.addProperty("money", money);
                json.addProperty("items", items);

                jsons.add(json);
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String stringToWrite = gson.toJson(jsons);

        // Using PrintWriter because it truncates the file if it exists or creates a new one if it doesn't
        // And truncation is good because we don't want dead mercs respawning
        try {
            writer = new PrintWriter(getResourcePath(MERC_SAVE_FILE), "UTF-8");
            writer.write(stringToWrite);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            logger.error("Could not save mercenaries, " + e.getMessage());
        }
    }

    /**
     * @param fileName The name of the file to get the resource path of
     * @return The path in the resource folder to the given file
     */
    public static String getResourcePath(String fileName) {
        if (DebugOptions.DEV_ROOT_PATH != null) {
            return DebugOptions.DEV_ROOT_PATH + fileName;
        } else {
            return "src/main/resources/" + fileName;
        }
    }

    /**
     * Checks if a resource exists
     *
     * @param fileName Just the name of the resource, not the path
     * @return A boolean corresponding to the resources existence
     */
    public static boolean resourceExists(String fileName) {
        String path = getResourcePath(fileName);

        return new FileHandle(Paths.get(path).toFile()).exists();
    }

    /**
     * Tests is the game has a previous ship (a game to continue)
     */
    public static boolean hasPrevShip(String fileName) {
        return resourceExists(fileName);
    }

    /**
     * Load last saved ship from file
     */
    public static ShipConfig readShip(HullConfigManager hullConfigs, ItemManager itemManager) {
        IniReader ir = new IniReader(SAVE_FILE_NAME, null);

        String hullName = ir.getString("hull", null);
        if (hullName == null) {
            return null;
        }

        HullConfig hull = hullConfigs.getConfig(hullName);
        if (hull == null) {
            return null;
        }

        int money = ir.getInt("money", 0);
        String itemsStr = ir.getString("items", "");

        float x = ir.getFloat("x", 0);
        float y = ir.getFloat("y", 0);
        Vector2 spawnPos = new Vector2(x, y);

        return new ShipConfig(hull, itemsStr, money, 1, null, itemManager, spawnPos);
    }

    /**
     * Saves the world to a file. Currently stores the seed used to generate the world and the number of systems
     * @param numberOfSystems
     */
    public static void saveWorld(int numberOfSystems) {
        Long seed = SolRandom.getSeed();
        String fileName = SaveManager.getResourcePath(WORLD_SAVE_FILE_NAME);

        JsonObject world = new JsonObject();
        world.addProperty("seed", seed);
        world.addProperty("systems", numberOfSystems);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String stringToWrite = gson.toJson(world);

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
            writer.write(stringToWrite);
            logger.debug("Successfully saved the world file");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            logger.error("Could not save world file", e);
            return;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Load the last saved world from file, or returns null if there is no file
     */
    public static WorldConfig loadWorld() {
        if (SaveManager.resourceExists(WORLD_SAVE_FILE_NAME)) {
            WorldConfig config = new WorldConfig();
            JsonReader reader = null;
            try {
                reader = new com.google.gson.stream.JsonReader(new FileReader(SaveManager.getResourcePath(WORLD_SAVE_FILE_NAME)));
                reader.setLenient(true); // without this it will fail with strange errors
                JsonObject world = new JsonParser().parse(reader).getAsJsonObject();

                if (world.has("seed")) {
                    config.setSeed(world.get("seed").getAsLong());
                }

                if (world.has("systems")) {
                    config.setNumberOfSystems(world.get("systems").getAsInt());
                }

                logger.debug("Successfully loaded the world file");
                return config;
            } catch (FileNotFoundException e) {
                logger.error("Cannot find world file", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // ignore exception
                    }
                }
            }
        }

        return null;
    }
}
