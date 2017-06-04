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
package org.destinationsol.game.item;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.projectile.ProjectileConfig;
import org.terasology.assets.ResourceUrn;

public class Clip implements SolItem {
    private final Config myConfig;

    public Clip(Config config) {
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

    public Config getConfig() {
        return myConfig;
    }

    @Override
    public SolItem copy() {
        return new Clip(myConfig);
    }

    @Override
    public boolean isSame(SolItem item) {
        return item instanceof Clip && ((Clip) item).myConfig == myConfig;
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
        public final int price;
        public final String displayName;
        public final String desc;
        public final int size;
        public final TextureAtlas.AtlasRegion icon;
        public final ProjectileConfig projConfig;
        public final boolean infinite;
        public final int projectilesPerShot;
        public final SolItemType itemType;
        public final String plural;
        public final String code;
        public final Clip example;

        public Config(ProjectileConfig projConfig, boolean infinite, int price, String displayName, int size,
                          String plural, TextureAtlas.AtlasRegion icon, int projectilesPerShot, SolItemType itemType, String code) {
            this.projConfig = projConfig;
            this.infinite = infinite;
            this.price = price;
            this.displayName = displayName;
            this.size = size;
            this.icon = icon;
            this.projectilesPerShot = projectilesPerShot;
            this.itemType = itemType;
            this.plural = plural;
            this.code = code;
            this.desc = size + " " + this.plural;
            this.example = new Clip(this);
        }

        public static void load(ResourceUrn clipName, ItemManager itemManager, SolItemTypes types) {
            Json json = Assets.getJson(clipName);
            JsonValue rootNode = json.getJsonValue();

            String projectileName = rootNode.getString("projectile");
            ProjectileConfig projectileConfig = itemManager.projConfigs.find(projectileName);
            boolean infinite = rootNode.getBoolean("infinite", false);
            int size = rootNode.getInt("size");
            int projectilesPerShot = rootNode.getInt("projectilesPerShot", 1);
            if (projectilesPerShot < 1) {
                throw new AssertionError("Invalid projectilesPerShot for " + clipName);
            }

            int price = 0;
            String displayName = "";
            String plural = "";
            TextureAtlas.AtlasRegion icon = null;
            if (!infinite) {
                price = rootNode.getInt("price");
                displayName = rootNode.getString("displayName");
                plural = rootNode.getString("plural");
                icon = Assets.getAtlasRegion(new ResourceUrn(clipName + "Icon"));
            }

            json.dispose();

            Config clipConfig = new Config(projectileConfig, infinite, price, displayName, size, plural, icon, projectilesPerShot, types.clip, clipName.toString());
            itemManager.registerItem(clipConfig.example);
        }
    }
}
