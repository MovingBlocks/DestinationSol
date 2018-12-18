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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.destinationsol.assets.json.Validator;
import org.json.JSONObject;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.game.SolGame;

public class AbilityCharge implements SolItem {
    private final Config config;

    AbilityCharge(Config config) {
        this.config = config;
    }

    @Override
    public String getDisplayName() {
        return config.displayName;
    }

    @Override
    public float getPrice() {
        return config.price;
    }

    @Override
    public String getDescription() {
        return config.description;
    }

    @Override
    public SolItem copy() {
        return new AbilityCharge(config);
    }

    @Override
    public boolean isSame(SolItem item) {
        return item instanceof AbilityCharge && ((AbilityCharge) item).config == config;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(SolGame game) {
        return config.icon;
    }

    @Override
    public SolItemType getItemType() {
        return config.itemType;
    }

    @Override
    public String getCode() {
        return config.code;
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
        public final TextureAtlas.AtlasRegion icon;
        public final float price;
        public final String displayName;
        public final String description;
        public final AbilityCharge example;

        public Config(TextureAtlas.AtlasRegion icon, float price, String displayName, String description, SolItemType itemType,
                      String code) {
            this.icon = icon;
            this.price = price;
            this.displayName = displayName;
            this.description = description;
            this.itemType = itemType;
            this.code = code;
            this.example = new AbilityCharge(this);
        }

        public static void load(String abilityName, ItemManager itemManager, SolItemTypes types) {
            JSONObject rootNode = Validator.getValidatedJSON(abilityName, "engine:schemaAbilityCharges");

            float price = (float) rootNode.getDouble("price");
            String displayName = rootNode.getString("displayName");
            String desc = rootNode.getString("desc");

            TextureAtlas.AtlasRegion icon = Assets.getAtlasRegion(abilityName + "Icon");

            Config abilityConfig = new Config(icon, price, displayName, desc, types.abilityCharge, abilityName);
            itemManager.registerItem(abilityConfig.example);
        }
    }
}
