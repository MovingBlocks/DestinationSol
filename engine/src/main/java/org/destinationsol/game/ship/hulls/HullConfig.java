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
package org.destinationsol.game.ship.hulls;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.Immutable;
import org.destinationsol.game.item.Engine;
import org.destinationsol.game.ship.AbilityConfig;

import java.util.ArrayList;
import java.util.List;

@Immutable
public final class HullConfig {
    private final Data data;

    public HullConfig(Data configData) {
        this.data = new Data(configData);
    }

    private static List<Vector2> deepCopyOf(List<Vector2> src) {
        List<Vector2> returnList = new ArrayList<>(src.size());

        for (Vector2 vector : src) {
            returnList.add(new Vector2(vector));
        }

        return returnList;
    }

    public String getInternalName() {
        return data.internalName;
    }

    public float getSize() {
        return data.size;
    }

    public int getMaxLife() {
        return data.maxLife;
    }

    public Vector2 getE1Pos() {
        return new Vector2(data.e1Pos);
    }

    public Vector2 getE2Pos() {
        return new Vector2(data.e2Pos);
    }

    public GunSlot getGunSlot(int slotNr) {
        return data.gunSlots.get(slotNr);
    }

    public int getNrOfGunSlots() {
        return data.gunSlots.size();
    }

    public List<GunSlot> getGunSlotList() {
        return new ArrayList<>(data.gunSlots);
    }

    public List<Vector2> getLightSourcePositions() {
        return deepCopyOf(data.lightSrcPoss);
    }

    public float getDurability() {
        return data.durability;
    }

    public boolean hasBase() {
        return data.hasBase;
    }

    public List<Vector2> getForceBeaconPositions() {
        return deepCopyOf(data.forceBeaconPoss);
    }

    public List<Vector2> getDoorPositions() {
        return deepCopyOf(data.doorPoss);
    }

    public TextureAtlas.AtlasRegion getIcon() {
        return new TextureAtlas.AtlasRegion(data.icon);
    }

    public Type getType() {
        return data.type;
    }

    public TextureAtlas.AtlasRegion getTexture() {
        return new TextureAtlas.AtlasRegion(data.tex);
    }

    public Engine.Config getEngineConfig() {
        return data.engineConfig;
    }

    public AbilityConfig getAbility() {
        return data.ability;
    }

    public float getApproxRadius() {
        return data.approxRadius;
    }

    public String getDisplayName() {
        return data.displayName;
    }

    public float getPrice() {
        return data.price;
    }

    public float getHirePrice() {
        return data.hirePrice;
    }

    public Vector2 getOrigin() {
        return new Vector2(data.origin);
    }

    public Vector2 getShipBuilderOrigin() {
        return new Vector2(data.shipBuilderOrigin);
    }

    public enum Type {
        STD("std"),
        BIG("big"),
        STATION("station");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public static Type forName(String name) {
            for (Type t : Type.values()) {
                if (t.name.equals(name)) {
                    return t;
                }
            }

            return null;
        }
    }

    public final static class Data {

        public String internalName;
        public float size;
        public int maxLife;
        public Vector2 e1Pos;
        public Vector2 e2Pos;
        public List<GunSlot> gunSlots = new ArrayList<>();
        public List<Vector2> lightSrcPoss = new ArrayList<>();
        public float durability;
        public boolean hasBase;
        public List<Vector2> forceBeaconPoss = new ArrayList<>();
        public List<Vector2> doorPoss = new ArrayList<>();
        public TextureAtlas.AtlasRegion icon;
        public Type type;
        public TextureAtlas.AtlasRegion tex;
        public Engine.Config engineConfig;
        public AbilityConfig ability;
        public float approxRadius;
        public String displayName;
        public float price;
        public float hirePrice;
        // origin is the value after it has been processed
        public Vector2 origin = new Vector2();
        // shipBuilderOrigin is the vector loaded from the file
        public Vector2 shipBuilderOrigin = new Vector2();

        public Data() {

        }

        public Data(Data src) {
            this.internalName = src.internalName;
            this.size = src.size;
            this.maxLife = src.maxLife;
            this.e1Pos = new Vector2(src.e1Pos);
            this.e2Pos = new Vector2(src.e2Pos);
            this.lightSrcPoss = deepCopyOf(src.lightSrcPoss);
            this.durability = src.durability;
            this.hasBase = src.hasBase;
            this.forceBeaconPoss = deepCopyOf(src.forceBeaconPoss);
            this.doorPoss = deepCopyOf(src.doorPoss);
            this.icon = new TextureAtlas.AtlasRegion(src.icon);
            this.type = src.type;
            this.tex = new TextureAtlas.AtlasRegion(src.tex);
            this.engineConfig = src.engineConfig;
            this.ability = (src.ability == null) ? null : src.ability;
            this.approxRadius = src.approxRadius;
            this.displayName = src.displayName;
            this.price = src.price;
            this.hirePrice = src.hirePrice;
            this.origin = new Vector2(src.origin);
            this.shipBuilderOrigin = new Vector2(src.shipBuilderOrigin);
            this.gunSlots.addAll(src.gunSlots);
        }

    }
}
