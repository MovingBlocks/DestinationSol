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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.MercItem;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Loader for mercenary data from a json file in the save directory.
 */
class MercenarySaveLoader {
    private static final float MERCENARY_SHIP_DENSITY = -1f;
    private static final String NODE_HULL = "hull";
    private static final String NODE_ITEMS = "items";
    private static final String NODE_MONEY = "money";
    private static Logger logger = LoggerFactory.getLogger(MercenarySaveLoader.class);

    /**
     * Loads mercenaries from a save file in json format.
     * If the file does not exist or is empty an empty list is returned.
     *
     * @param hullConfigManager The config manager to resolve ship hulls.
     * @param itemManager       The item manager to be used in each ship config.
     * @param fileName          Name of the save file without a path. The file will be resolved using the {@link SaveManager}.
     * @return A list of all loaded mercenaries.
     */
    List<MercItem> loadMercenariesFromSave(HullConfigManager hullConfigManager, ItemManager itemManager, String fileName) {
        if (!SaveManager.resourceExists(fileName)) {
            return emptyList();
        }
        String path = SaveManager.getResourcePath(fileName);
        if (new File(path).length() == 0) {
            return emptyList();
        }
        ArrayList<HashMap<String, String>> mercenaries = loadMercenariesDataFromJson(path);
        return toMercenaryItems(mercenaries, hullConfigManager, itemManager);
    }

    private ArrayList<HashMap<String, String>> loadMercenariesDataFromJson(String path) {
        ArrayList<HashMap<String, String>> mercenaries = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
            }.getType();
            mercenaries = gson.fromJson(bufferedReader, type);
        } catch (IOException e) {
            logger.error("Could not load mercenaries!", e);
        }
        return mercenaries;
    }

    private List<MercItem> toMercenaryItems(ArrayList<HashMap<String, String>> mercenaries, HullConfigManager hullConfigManager, ItemManager itemManager) {
        List<MercItem> mercenaryItems = new ArrayList<>();
        for (HashMap<String, String> node : mercenaries) {
            HullConfig hullConfig = hullConfigManager.getConfig(node.get(NODE_HULL));
            String items = node.get(NODE_ITEMS);
            int money = Integer.parseInt(node.get(NODE_MONEY));
            MercItem mercenaryItem = new MercItem(createShipConfig(hullConfig, items, money, itemManager));
            mercenaryItems.add(mercenaryItem);
        }
        return mercenaryItems;
    }

    private ShipConfig createShipConfig(HullConfig hullConfig, String items, int money, ItemManager itemManager) {
        return new ShipConfig(hullConfig, items, money, MERCENARY_SHIP_DENSITY, null, itemManager);
    }
}
