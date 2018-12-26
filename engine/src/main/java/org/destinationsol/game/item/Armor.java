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
import org.destinationsol.assets.audio.OggSoundManager;
import org.destinationsol.assets.audio.OggSoundSet;
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.assets.json.Json;
import org.destinationsol.game.DmgType;
import org.destinationsol.game.SolGame;

import java.util.Arrays;
import java.util.List;

public class Armor implements SolItem {
    private final Config config;
    private int myEquipped;

    private Armor(Config config) {
        this.config = config;
    }

    private Armor(Config config, int equipped) {
        this(config);
        myEquipped = equipped;
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
        return config.desc;
    }

    @Override
    public SolItem copy() {
        return new Armor(config, myEquipped);
    }

    @Override
    public boolean isSame(SolItem item) {
        return false;
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

    public float getPerc() {
        return config.perc;
    }

    public PlayableSound getHitSound(DmgType dmgType) {
        switch (dmgType) {
            case BULLET:
                return config.bulletHitSound;
            case ENERGY:
                return config.energyHitSound;
        }
        return null;
    }

    public int isEquipped() {
        return myEquipped;
    }

    public void setEquipped(int equipped) {
        myEquipped = equipped;
    }

    public static class Config {
        public final String displayName;
        public final int price;
        public final float perc;
        public final String desc;
        public final PlayableSound bulletHitSound;
        public final TextureAtlas.AtlasRegion icon;
        public final PlayableSound energyHitSound;
        public final SolItemType itemType;
        public final String code;
        public final Armor example;

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

        public static void load(String armorName, ItemManager itemManager, OggSoundManager soundManager, SolItemTypes types) {
            JSONObject rootNode = Validator.getValidatedJSON(armorName, "engine:schemaArmor");

            String displayName = rootNode.getString("displayName");
            int price = rootNode.getInt("price");
            float perc = (float) rootNode.getDouble("perc");
            List<String> bulletDamageSoundUrns = Assets.convertToStringList(rootNode.getJSONArray("bulletHitSounds"));
            List<String> energyDamageSoundUrns = Assets.convertToStringList(rootNode.getJSONArray("energyHitSounds"));
            float basePitch = (float) rootNode.getDouble("baseSoundPitch");
            OggSoundSet bulletDmgSound = new OggSoundSet(soundManager, bulletDamageSoundUrns, basePitch);
            OggSoundSet energyDmgSound = new OggSoundSet(soundManager, energyDamageSoundUrns, basePitch);

            TextureAtlas.AtlasRegion icon = Assets.getAtlasRegion(armorName + "Icon");

            Config armorConfig = new Config(displayName, price, perc, bulletDmgSound, icon, energyDmgSound, types.armor, armorName);
            itemManager.registerItem(armorConfig.example);
        }
    }
}
