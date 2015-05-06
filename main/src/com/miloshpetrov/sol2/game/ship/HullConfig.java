package com.miloshpetrov.sol2.game.ship;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.item.EngineItem;

import java.util.ArrayList;
import java.util.List;

public final class HullConfig {

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

    public Vector2 getG1Pos() {
        return (data.g1Pos == null) ? null : new Vector2(data.g1Pos);
    }

    public Vector2 getG2Pos() {
        return (data.g2Pos == null) ? null : new Vector2(data.g2Pos);
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

    public EngineItem.Config getEngineConfig() {
        return data.engineConfig;
    }

    public AbilityConfig getAbility() {
        return data.ability;
    }

    public float getApproxRadius() {
        return data.approxRadius;
    }

    public boolean g1IsUnderShip() {
        return data.g1UnderShip;
    }

    public boolean g2IsUnderShip() {
        return data.g2UnderShip;
    }

    public boolean m1IsFixed() {
        return data.m1Fixed;
    }

    public boolean m2IsFixed() {
        return data.m2Fixed;
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

    private static List<Vector2> deepCopyOf(List<Vector2> src) {
        List<Vector2> returnList = new ArrayList<Vector2>(src.size());

        for(Vector2 vector: src) {
            returnList.add(new Vector2(vector));
        }

        return returnList;
    }

    private final Data data;

    public HullConfig(Data configData)
    {
        this.data = new Data(configData);
    }

    public final static class Data {

        public String internalName;
        public float size;
        public int maxLife;
        public Vector2 e1Pos;
        public Vector2 e2Pos;
        public Vector2 g1Pos;
        public Vector2 g2Pos;
        public List<Vector2> lightSrcPoss = new ArrayList<Vector2>();
        public float durability;
        public boolean hasBase;
        public List<Vector2> forceBeaconPoss = new ArrayList<Vector2>();
        public List<Vector2> doorPoss = new ArrayList<Vector2>();
        public TextureAtlas.AtlasRegion icon;
        public Type type;
        public TextureAtlas.AtlasRegion tex;
        public EngineItem.Config engineConfig;
        public AbilityConfig ability;
        public float approxRadius;
        public boolean g1UnderShip;
        public boolean g2UnderShip;
        public boolean m1Fixed;
        public boolean m2Fixed;
        public String displayName;
        public float price;
        public float hirePrice;
        public Vector2 origin = new Vector2();

        public Data() {

        }

        public Data(Data src) {
            this.internalName = src.internalName;
            this.size = src.size;
            this.maxLife = src.maxLife;
            this.e1Pos = new Vector2(src.e1Pos);
            this.e2Pos = new Vector2(src.e2Pos);
            this.g1Pos = (src.g1Pos == null) ? null : new Vector2(src.g1Pos);
            this.g2Pos = (src.g2Pos == null) ? null : new Vector2(src.g2Pos);
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
            this.g1UnderShip = src.g1UnderShip;
            this.g2UnderShip = src.g2UnderShip;
            this.m1Fixed = src.m1Fixed;
            this.m2Fixed = src.m2Fixed;
            this.displayName = src.displayName;
            this.price = src.price;
            this.hirePrice = src.hirePrice;
            this.origin = new Vector2(src.origin);
        }

    }

    public static enum Type {
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
}
