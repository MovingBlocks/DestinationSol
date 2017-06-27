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
import org.destinationsol.assets.audio.PlayableSound;
import org.destinationsol.assets.json.Json;
import org.destinationsol.game.GameColors;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.particle.EffectConfig;
import org.destinationsol.game.particle.EffectTypes;
import org.destinationsol.game.sound.OggSoundManager;
import org.destinationsol.game.sound.OggSoundSet;

import java.util.Arrays;
import java.util.List;

public class Engine implements SolItem {
    private final Config myConfig;

    private Engine(Config config) {
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

    public float getRotAcc() {
        return myConfig.rotAcc;
    }

    public float getAcc() {
        return myConfig.acc;
    }

    public float getMaxRotSpd() {
        return myConfig.maxRotSpd;
    }

    public boolean isBig() {
        return myConfig.big;
    }

    @Override
    public Engine copy() {
        return new Engine(myConfig);
    }

    @Override
    public boolean isSame(SolItem item) {
        return item instanceof Engine && ((Engine) item).myConfig == myConfig;
    }

    @Override
    public TextureAtlas.AtlasRegion getIcon(SolGame game) {
        return myConfig.icon;
    }

    @Override
    public SolItemType getItemType() {
        return null;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public int isEquipped() {
        return 0;
    }

    @Override
    public void setEquipped(int equipped) {

    }

    public PlayableSound getWorkSound() {
        return myConfig.workSound;
    }

    public EffectConfig getEffectConfig() {
        return myConfig.effectConfig;
    }

    public static class Config {
        public final String displayName;
        public final int price;
        public final String desc;
        public final float rotAcc;
        public final float acc;
        public final float maxRotSpd;
        public final boolean big;
        public final Engine example;
        public final PlayableSound workSound;
        public final TextureAtlas.AtlasRegion icon;
        public final EffectConfig effectConfig;
        public final String code;

        private Config(String displayName, int price, String desc, float rotAcc, float acc, float maxRotSpd, boolean big,
                       PlayableSound workSound, TextureAtlas.AtlasRegion icon, EffectConfig effectConfig, String code) {
            this.displayName = displayName;
            this.price = price;
            this.desc = desc;
            this.rotAcc = rotAcc;
            this.acc = acc;
            this.maxRotSpd = maxRotSpd;
            this.big = big;
            this.workSound = workSound;
            this.icon = icon;
            this.effectConfig = effectConfig;
            this.code = code;
            this.example = new Engine(this);
        }

        public static Config load(String engineName, OggSoundManager soundManager, EffectTypes effectTypes, GameColors cols) {
            Json json = Assets.getJson(engineName);
            JsonValue rootNode = json.getJsonValue();

            boolean big = rootNode.getBoolean("big");
            float rotAcc = big ? 100f : 515f;
            float acc = 2f;
            float maxRotSpd = big ? 40f : 230f;
            List<String> workSoundUrns = Arrays.asList(rootNode.get("workSounds").asStringArray());
            OggSoundSet workSoundSet = new OggSoundSet(soundManager, workSoundUrns);
            EffectConfig effectConfig = EffectConfig.load(rootNode.get("effect"), effectTypes, cols);

            json.dispose();

            // TODO: VAMPCAT: The icon / displayName was initially set to null. Is that correct?

            return new Config(null, 0, null, rotAcc, acc, maxRotSpd, big, workSoundSet, null, effectConfig, engineName);
        }
    }
}
