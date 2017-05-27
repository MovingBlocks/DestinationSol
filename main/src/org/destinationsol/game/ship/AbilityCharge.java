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

package org.destinationsol.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.AssetHelper;
import org.destinationsol.assets.json.Json;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.item.ItemManager;
import org.destinationsol.game.item.SolItem;
import org.destinationsol.game.item.SolItemType;
import org.destinationsol.game.item.SolItemTypes;
import org.terasology.assets.ResourceUrn;

public class AbilityCharge implements SolItem {
    private final Config myConfig;

    public AbilityCharge(Config config) {
        myConfig = config;
    }

    @Override
    public String getDisplayName() {
        return myConfig.displayName;
    }

    @Override
    public float getPrice() {
        return myConfig.price;
    }

    @Override
    public String getDesc() {
        return myConfig.desc;
    }

    @Override
    public SolItem copy() {
        return new AbilityCharge(myConfig);
    }

    @Override
    public boolean isSame(SolItem item) {
        return item instanceof AbilityCharge && ((AbilityCharge) item).myConfig == myConfig;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(SolGame game) {
        return myConfig.icon;
    }

    @Override
    public SolItemType getItemType() {
        return myConfig.itemType;
    }

    @Override
    public String getCode() {
        return myConfig.code;
    }

    @Override
    public int isEquipped() {
        return 0;
    }

    @Override
    public void setEquipped(int equipped) { }

    public static class Config {
        public final SolItemType itemType;
        public final String code;
        private final TextureAtlas.AtlasRegion icon;
        private final float price;
        private final String displayName;
        private final String desc;

        public Config(TextureAtlas.AtlasRegion icon, float price, String displayName, String desc, SolItemType itemType,
                      String code) {
            this.icon = icon;
            this.price = price;
            this.displayName = displayName;
            this.desc = desc;
            this.itemType = itemType;
            this.code = code;
        }

        public static void load(ResourceUrn abilityName, ItemManager itemManager, SolItemTypes types, AssetHelper assetHelper) {
            Json json = assetHelper.getJson(abilityName).get();
            JsonValue rootNode = json.getJsonValue();

            TextureAtlas.AtlasRegion icon = assetHelper.getAtlasRegion(new ResourceUrn(abilityName + "Icon"));
            float price = rootNode.getFloat("price");
            String displayName = rootNode.getString("displayName");
            String desc = rootNode.getString("desc");

            Config ability = new Config(icon, price, displayName, desc, types.abilityCharge, abilityName.toString());

            AbilityCharge abilityChargeExample = new AbilityCharge(ability);
            itemManager.registerItem(abilityChargeExample);

            json.dispose();
        }
    }
}
