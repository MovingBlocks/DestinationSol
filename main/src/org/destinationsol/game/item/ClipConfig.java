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

package org.destinationsol.game.item;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.files.FileManager;
import org.destinationsol.game.projectile.ProjectileConfig;

public class ClipConfig {
    public final int price;
    public final String displayName;
    public final String desc;
    public final int size;
    public final ClipItem example;
    public final TextureAtlas.AtlasRegion icon;
    public final ProjectileConfig projConfig;
    public final boolean infinite;
    public final int projectilesPerShot;
    public final SolItemType itemType;
    public final String plural;
    public final String code;

    public ClipConfig(ProjectileConfig projConfig, boolean infinite, int price, String displayName, int size,
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
        this.example = new ClipItem(this);
    }

    public static void load(ItemManager itemManager, TextureManager textureManager, SolItemTypes types) {
        JsonReader r = new JsonReader();
        FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("clips.json");
        JsonValue parsed = r.parse(configFile);
        for (JsonValue sh : parsed) {
            String projectileName = sh.getString("projectile");
            ProjectileConfig projConfig = itemManager.projConfigs.find(projectileName);
            boolean infinite = sh.getBoolean("infinite", false);
            int size = sh.getInt("size");
            int projectilesPerShot = sh.getInt("projectilesPerShot", 1);
            if (projectilesPerShot < 1) throw new AssertionError("projectiles per shot");

            int price = 0;
            String displayName = "";
            String plural = "";
            TextureAtlas.AtlasRegion icon = null;
            if (!infinite) {
                String iconName = sh.getString("iconName");
                price = sh.getInt("price");
                displayName = sh.getString("displayName");
                plural = sh.getString("plural");
                icon = textureManager.getTexture(TextureManager.ICONS_DIR + iconName);
            }
            String code = sh.name;
            ClipConfig config = new ClipConfig(projConfig, infinite, price, displayName, size, plural, icon, projectilesPerShot, types.clip, code);
            itemManager.registerItem(config.example);
        }
    }
}
