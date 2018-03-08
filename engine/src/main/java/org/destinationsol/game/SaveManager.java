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
package org.destinationsol.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.destinationsol.IniReader;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.item.*;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private static Logger logger = LoggerFactory.getLogger(SaveManager.class);
    
    private static final String SAVE_FILE_NAME = "prevShip.ini";
    static String MERC_SAVE_FILE = getResourcePath("mercenaries.json");

    private static final String FILE_NAME = "prevShip.ini";

    public static void writeShips(HullConfig hull, float money, ArrayList<SolItem> itemsList, SolGame game) {
        String hullName = game.getHullConfigs().getName(hull);

        writeMercs(game);

        String items = itemsToString(itemsList);
        
        Vector2 pos = game.getHero().getPosition();
        
        IniReader.write(FILE_NAME, "hull", hullName, "money", (int) money, "items", items, "x", pos.x, "y", pos.y);
    }

    /**
     * Encodes the given list of SolItems as a string.
     * @param items A list of SolItems to be encoded as a string
     * @return A string of items suitable for saving
     */
    private static String itemsToString(ArrayList<SolItem> items) {
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
                if (g.ammo > 0 && !g.config.clipConf.infinite) {
                    sb.append(g.config.clipConf.code).append(" ");
                }
            }
        }

        return sb.toString();

    }

    /**
     * Writes the player's mercenaries to their JSON file.
     * The file will be created if it doesn't exist.
     * @param game The instance of the game we're dealing with
     */
    private static void writeMercs(SolGame game) {
        PrintWriter writer;
        
        ItemContainer mercenaries = game.getHero().getShipUnchecked().getTradeContainer().getMercs();
        
        List<JsonObject> jsons = new ArrayList<JsonObject>();
        
        for (List<SolItem> group : mercenaries) {
            for (SolItem item : group) {
                SolShip merc = ((MercItem) item).getSolShip();
                // Json fields
                String hullName = game.getHullConfigs().getName(merc.getHull().config);
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
            writer = new PrintWriter(MERC_SAVE_FILE, "UTF-8");
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

    public static ShipConfig readShip(HullConfigManager hullConfigs, ItemManager itemManager, SolGame game) {
        IniReader ir = new IniReader(SAVE_FILE_NAME, null);

        String hullName = ir.getString("hull", null);
        if (hullName == null) {
            return null;
        }

        game.setShipName(hullName);

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
}
