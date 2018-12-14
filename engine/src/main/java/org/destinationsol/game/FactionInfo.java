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

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.ship.SolShip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class FactionInfo {

    private static ArrayList<String> factionName = new ArrayList<String>();
    private static ArrayList<String> factionColor = new ArrayList<String>();
    private static ArrayList<Integer> factionDisposition = new ArrayList<Integer>();
    public FactionInfo() {
        createFactionList();

    }

    private void createFactionList(){
        String[] folderList = getFolderList();

        for(int i = 0; i < folderList.length; i++) {
            File factionFile = new File("modules/" + folderList[i] + "/assets/configs/factions.JSON");

            JsonParser parser = new JsonParser();
            try {
                FileReader fileReader = new FileReader(factionFile);
                JsonArray factionJSON = parser.parse(fileReader).getAsJsonArray();
                for(int n = 0; n < factionJSON.size(); n++) {
                    factionName.add(factionJSON.get(n).getAsJsonObject().get("name").toString().replace("\"", ""));
                    factionColor.add(factionJSON.get(n).getAsJsonObject().get("color").toString().replace("\"", ""));
                    factionDisposition.add(factionJSON.get(n).getAsJsonObject().get("disposition").getAsInt());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


    }


    private static String[] getFolderList(){

        File file = new File("modules/");
        String[] folderList = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        return folderList;
    }


    public static ArrayList getFactionNames(){
        return factionName;
    }

    public static ArrayList getFactionColors(){
        return factionColor;
    }

    public static int getFactionID(SolShip ship){
        String shipName = ship.getHull().getHullConfig().getInternalName();
        String[] folderList = getFolderList();

        for(int i = 0; i < folderList.length; i++) {
            File factionFile = new File("modules/" + folderList[i] + "/assets/configs/factions.JSON");
            JsonParser parser = new JsonParser();
            shipName = shipName.replaceFirst(folderList[i], "");
            shipName = shipName.replace(":", "");
            try {
                FileReader fileReader = new FileReader(factionFile);
                JsonArray factionJSON = parser.parse(fileReader).getAsJsonArray();

                for(int n = 0; n < factionJSON.size(); n++) {
                    for(int z = 0; z < factionJSON.get(n).getAsJsonObject().get("ships").getAsJsonArray().size(); z++)
                        if(shipName.equals(factionJSON.get(n).getAsJsonObject().get("ships").getAsJsonArray().get(z).getAsString())) {
                            return n;
                        }

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static ArrayList<Integer> getDisposition(){
        return factionDisposition;
    }

    public static void setDisposition(int n, int num){
        if(factionDisposition.get(n) <= 100)
             factionDisposition.set(n, factionDisposition.get(n) + num);
    }


}




