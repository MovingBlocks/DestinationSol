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
package org.destinationsol.game.item;

import org.json.JSONObject;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.ship.hulls.HullConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TradeConfig {
    public final List<ItemConfig> items = new ArrayList<>();
    public final ItemContainer hulls = new ItemContainer();
    public final ItemContainer mercs = new ItemContainer();

    public void load(JSONObject tradeNode, HullConfigManager hullConfigs, ItemManager itemManager) {
        if (tradeNode == null) {
            return;
        }

        String itemStr = tradeNode.getString("items");
        List<ItemConfig> itemList = itemManager.parseItems(itemStr);
        Collections.reverse(itemList); // TODO: Examine why this is required.
        items.addAll(itemList);

        String shipStr = tradeNode.optString("ships", "");
        String[] split = shipStr.split(" ");
        for (int i = split.length - 1; i >= 0; i--) {
            String hullName = split[i];
            HullConfig hull = hullConfigs.getConfig(hullName);
            hulls.add(new ShipItem(hull));
        }

        ArrayList<ShipConfig> loadList = ShipConfig.loadList(tradeNode.getJSONArray("mercenaries"), hullConfigs, itemManager);
        for (int i = loadList.size() - 1; i >= 0; i--) {
            ShipConfig merc = loadList.get(i);
            mercs.add(new MercItem(merc));
        }
    }

}
