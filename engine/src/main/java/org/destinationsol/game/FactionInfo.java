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
import org.destinationsol.game.ship.SolShip;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.Set;

public class FactionInfo {

    private static ArrayList<String> factionName = new ArrayList<String>();
    private static ArrayList<String> factionColor = new ArrayList<String>();
    private static ArrayList<Integer> factionDisposition = new ArrayList<Integer>();

    public FactionInfo() {
        createFactionList();
    }

    private void createFactionList() {
        for (String module : getModuleList()) {
            String path = module.replaceAll("[[, ]]", "") + ":factions";
            Json factionJson = Assets.getJson(path);
            for (int n = 0; n < factionJson.getJsonValue().getJSONArray("factions").length(); n++) {
                factionName.add(factionJson.getJsonValue().getJSONArray("factions").getJSONObject(n).getString("name").replace("\"", ""));
                factionColor.add(factionJson.getJsonValue().getJSONArray("factions").getJSONObject(n).getString("color").replace("\"", ""));
                factionDisposition.add(factionJson.getJsonValue().getJSONArray("factions").getJSONObject(n).getInt("disposition"));
            }
        }
    }

    private static ArrayList<String> getModuleList() {
        ArrayList<String> moduleList = new ArrayList<String>();
        Set<ResourceUrn> moduleUrn = Assets.getAssetHelper().list(Json.class, "[a-zA-Z0-9]*:factions");
        for(ResourceUrn module : moduleUrn) {
            moduleList.add(module.getModuleName().toString());
        }
        return moduleList;
    }

    public static ArrayList getFactionNames() {
        return factionName;
    }

    public static ArrayList getFactionColors() {
        return factionColor;
    }

    public static int getFactionID(SolShip ship) {
        String shipName = ship.getHull().getHullConfig().getInternalName();
        for(String module: getModuleList()) {
            String path =  module.replaceAll("[[, ]]", "") + ":factions";
            Json factionJson = Assets.getJson(path);
            shipName = shipName.replaceFirst(module, "");
            shipName = shipName.replace(":", "");
            for(int n = 0; n < factionJson.getJsonValue().length(); n++) {
                for(int z = 0; z < factionJson.getJsonValue().getJSONArray("factions").getJSONObject(n).getJSONArray("ships").length(); z++) {
                    if(shipName.equals(factionJson.getJsonValue().getJSONArray("factions").getJSONObject(n).getJSONArray("ships").get(z))) {
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
        if(factionDisposition.get(n) <= 100) {
            factionDisposition.set(n, factionDisposition.get(n) + num);
        }
    }
}
