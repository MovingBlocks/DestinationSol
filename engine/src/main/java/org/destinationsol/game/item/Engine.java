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
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.game.SolGame;
import org.json.JSONObject;

public class Engine implements SolItem {
    private static final TextureAtlas.AtlasRegion DEFAULT_ENGINE_ICON = Assets.getAtlasRegion("engine:iconEngine");
    private final Config config;
    private int equipped;

    private Engine(Config config) {
        this.config = config;
    }

    private Engine(Config config, int equipped) {
        this.config = config;
        this.equipped = equipped;
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

    public float getRotationAcceleration() {
        return config.rotationAcceleration;
    }

    public float getAcceleration() {
        return config.acceleration;
    }

    public float getMaxRotationSpeed() {
        return config.maxRotationSpeed;
    }

    public boolean isBig() {
        return config.isBig;
    }

    @Override
    public Engine copy() {
        return new Engine(config, equipped);
    }

    @Override
    public boolean isSame(SolItem item) {
        return item instanceof Engine && ((Engine) item).config == config;
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
        return this.equipped;
    }

    @Override
    public void setEquipped(int equipped) {
        this.equipped = equipped;
    }

    public static class Config {
        public final String displayName;
        public final int price;
        public final String description;
        public final float rotationAcceleration;
        public final float acceleration;
        public final float maxRotationSpeed;
        public final boolean isBig;
        public final Engine exampleEngine;
        public final TextureAtlas.AtlasRegion icon;
        public final String code;
        public final SolItemType itemType;

        private Config(String displayName, int price, String description, float rotationAcceleration, float acceleration, float maxRotationSpeed, boolean isBig,
                       TextureAtlas.AtlasRegion icon, String code, ItemManager itemManager, SolItemType itemType) {
            this.displayName = displayName;
            this.price = price;
            this.description = description + "\n\n" +
                    "Acceleration: " + acceleration + " m/s^2\n" +
                    "Rotational Acceleration: " + rotationAcceleration + " rad/s^2\n" +
                    "Maximum Angular Velocity: " + maxRotationSpeed + " rad/s";
            this.rotationAcceleration = rotationAcceleration;
            this.acceleration = acceleration;
            this.maxRotationSpeed = maxRotationSpeed;
            this.isBig = isBig;
            this.icon = icon;
            this.code = code;
            this.itemType = itemType;
            this.exampleEngine = new Engine(this);
            itemManager.registerItem(exampleEngine);
        }

        public static Config load(String engineName, ItemManager itemManager, SolItemTypes types) {
            JSONObject rootNode = Validator.getValidatedJSON(engineName, "engine:schemaEngine");

            boolean isBig = rootNode.getBoolean("big");
            float rotationAcceleration = (float) rootNode.optDouble("rotationAcceleration", isBig ? 100f : 515f);
            float acceleration = (float) rootNode.optDouble("acceleration", 2f);
            float maxRotationSpeed = (float) rootNode.optDouble("maxRotationSpeed", isBig ? 40f : 230f);

            TextureAtlas.AtlasRegion icon;
            try {
                icon = Assets.getAtlasRegion(engineName + "Icon");
            } catch (RuntimeException ignore) {
                icon = DEFAULT_ENGINE_ICON;
            }
            return new Config(rootNode.optString("name", engineName), 0,
                    rootNode.optString("description", ""), rotationAcceleration, acceleration,
                    maxRotationSpeed, isBig, icon, engineName, itemManager, types.engine);
        }
    }
}
