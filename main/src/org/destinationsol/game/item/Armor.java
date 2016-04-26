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
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.files.FileManager;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.sound.OggSoundManager;
import org.destinationsol.game.sound.OggSoundSet;

import java.util.Arrays;
import java.util.List;

public class Armor implements SolItem {
    private final Config myConfig;
    private int equipped;

    private Armor(Config config) {
        myConfig = config;
    }

    private Armor(Config config, int equipped) {
        this(config);
        this.equipped = equipped;
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
        return new Armor(myConfig, equipped);
    }

    @Override
    public boolean isSame(SolItem item) {
        return false;
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
        return equipped;
    }

    @Override
    public void setEquipped(int equipped) {
        this.equipped = equipped;
    }

    public float getPerc() {
        return myConfig.perc;
    }

    public PlayableSound getHitSound(DmgType dmgType) {
        switch (dmgType) {
            case BULLET:
                return myConfig.bulletHitSound;
            case ENERGY:
                return myConfig.energyHitSound;
        }
        return null;
    }

    public static class Config {
        public final String displayName;
        public final int price;
        public final float perc;
        public final String desc;
        public final PlayableSound bulletHitSound;
        public final Armor example;
        public final TextureAtlas.AtlasRegion icon;
        public final PlayableSound energyHitSound;
        public final SolItemType itemType;
        public final String code;

        private Config(String displayName, int price, float perc, PlayableSound bulletHitSound,
                       TextureAtlas.AtlasRegion icon, PlayableSound energyHitSound, SolItemType itemType, String code) {
            this.displayName = displayName;
            this.price = price;
            this.perc = perc;
            this.icon = icon;
            this.energyHitSound = energyHitSound;
            this.itemType = itemType;
            this.code = code;
            this.desc = "Reduces damage by " + (int) (perc * 100) + "%\nStrong against energy guns";
            this.bulletHitSound = bulletHitSound;
            this.example = new Armor(this);
        }

        public static void loadConfigs(ItemManager itemManager, OggSoundManager soundManager, TextureManager textureManager, SolItemTypes types) {
            JsonReader r = new JsonReader();
            FileHandle configFile = FileManager.getInstance().getItemsDirectory().child("armors.json");
            JsonValue parsed = r.parse(configFile);
            for (JsonValue sh : parsed) {
                String displayName = sh.getString("displayName");
                int price = sh.getInt("price");
                float perc = sh.getFloat("perc");
                List<String> bulletDamageSoundUrns = Arrays.asList(sh.get("bulletHitSounds").asStringArray());
                List<String> energyDamageSoundUrns = Arrays.asList(sh.get("energyHitSounds").asStringArray());
                float basePitch = sh.getFloat("baseSoundPitch", 1);
                OggSoundSet bulletDmgSound = new OggSoundSet(soundManager, bulletDamageSoundUrns, basePitch);
                OggSoundSet energyDmgSound = new OggSoundSet(soundManager, energyDamageSoundUrns, basePitch);
                TextureAtlas.AtlasRegion icon = textureManager.getTex(TextureManager.ICONS_DIR + sh.getString("icon"), configFile);
                String code = sh.name;
                Config config = new Config(displayName, price, perc, bulletDmgSound, icon, energyDmgSound, types.armor, code);
                itemManager.registerItem(config.example);
            }
        }
    }
}
