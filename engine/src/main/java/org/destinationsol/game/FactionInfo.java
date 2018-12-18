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
import org.json.JSONArray;
import org.json.JSONTokener;
import org.json.JSONObject;

import java.io.FilenameFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class FactionInfo {

    private static ArrayList<String> factionName = new ArrayList<String>();
    private static ArrayList<String> factionColor = new ArrayList<String>();
    private static ArrayList<Integer> factionDisposition = new ArrayList<Integer>();

    public FactionInfo() {
        createFactionList();
    }

    private void createFactionList() {
        String[] moduleList = getModuleList();
        for(String module: moduleList) {
            String path =  module + ":factions";
            System.out.println(path.equals("case:factions"));
            System.out.println("core:factions");
            Json factionJSON = Assets.getJson("core:factions");
            for(int n = 0; n < factionJSON.getJsonValue().getJSONArray("factions").length(); n++) {
                factionName.add(factionJSON.getJsonValue().getJSONArray("factions").getJSONObject(n).getString("name").replace("\"", ""));
                factionColor.add(factionJSON.getJsonValue().getJSONArray("factions").getJSONObject(n).getString("color").replace("\"", ""));
                factionDisposition.add(factionJSON.getJsonValue().getJSONArray("factions").getJSONObject(n).getInt("disposition"));
            }
        }
    }

    private static String[] getModuleList() {
        File file = new File("modules/");
        String[] moduleList = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
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
        String[] moduleList = getModuleList();
        for(String module: moduleList) {
            String path =  module + ":factions";
            Json factionJSON = Assets.getJson(path);
                shipName = shipName.replaceFirst(module, "");
                shipName = shipName.replace(":", "");
                for(int n = 0; n < factionJSON.getJsonValue().length(); n++) {
                    for(int z = 0; z < factionJSON.getJsonValue().getJSONArray("factions").getJSONObject(n).getJSONArray("ships").length(); z++) {
                        if(shipName.equals(factionJSON.getJsonValue().getJSONArray("factions").getJSONObject(n).getJSONArray("ships").get(z))) {
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
        if(factionDisposition.get(n) <= 100)
             factionDisposition.set(n, factionDisposition.get(n) + num);
    }

}
