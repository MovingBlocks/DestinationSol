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
package org.destinationsol.game;

import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.destinationsol.game.ship.SolShip;
import org.json.JSONArray;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads ands stores faction data
 */
public class FactionInfo {
    private static ArrayList<String> factionName = new ArrayList<String>();
    private static ArrayList<String> factionColor = new ArrayList<String>();
    private static ArrayList<Integer> factionDisposition = new ArrayList<Integer>();

    public FactionInfo() {
        createFactionList();
    }

    private void createFactionList() {
        for (String modulePath : getModuleSet()) {
            Json factionJson = Assets.getJson(modulePath);
            Validator.getValidatedJSON(modulePath, "engine:schemaAbilitiesConfig");
            JSONArray factionJsonArray = factionJson.getJsonValue().getJSONArray("factions");
            for (int n = 0; n < factionJsonArray.length(); n++) {
                factionName.add(factionJsonArray.getJSONObject(n).getString("name").replace("\"", ""));
                factionColor.add(factionJsonArray.getJSONObject(n).getString("color").replace("\"", ""));
                factionDisposition.add(factionJsonArray.getJSONObject(n).getInt("disposition"));
            }
        }
    }

    private static Set<String> getModuleSet() {
        Set<String> moduleSet = new HashSet<String>();
        Set<ResourceUrn> moduleUrn = Assets.getAssetHelper().list(Json.class, "[a-zA-Z0-9]*:factions");
        for(ResourceUrn module : moduleUrn) {
            moduleSet.add(module.toString());
        }
        return moduleSet;
    }

    public static void clearValues() {
        factionName.clear();
        factionColor.clear();
        factionDisposition.clear();
    }

    public static ArrayList getFactionNames() {
        return factionName;
    }

    public static ArrayList getFactionColors() {
        return factionColor;
    }

    public static int getFactionID(SolShip ship) {
        String shipName = ship.getHull().getHullConfig().getInternalName();
        for(String modulePath: getModuleSet()) {
            Json factionJson = Assets.getJson(modulePath);
            Validator.getValidatedJSON(modulePath, "engine:schemaAbilitiesConfig");
            JSONArray factionJsonArray = factionJson.getJsonValue().getJSONArray("factions");
            shipName = shipName.replaceAll(".*:", "");
            for(int n = 0; n < factionJsonArray.length(); n++) {
                for(int z = 0; z < factionJsonArray.getJSONObject(n).getJSONArray("ships").length(); z++) {
                    if(shipName.equals(factionJsonArray.getJSONObject(n).getJSONArray("ships").get(z))) {
                        return n;
                    }
                }
            }
        }
        return 0;
    }

    public static ArrayList<Integer> getDisposition() {
        return factionDisposition;
    }

    public static void setDisposition(int n, int num) {
        if (factionDisposition.get(n) <= 100) {
            factionDisposition.set(n, factionDisposition.get(n) + num);
        }
    }
}
