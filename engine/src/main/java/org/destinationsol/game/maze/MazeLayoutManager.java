/*
 * Copyright 2021 The Terasology Foundation
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
package org.destinationsol.game.maze;

import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.terasology.gestalt.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class is used to load MazeLayouts from JSON files into MazeGenerators. This allows Mazes to have custom layouts.
 * A JSON maze layout has two properties: "name" which is a string and "inner", which is a 2D array of booleans which
 * represents where the maze should have tiles and should not have tiles. See spiralMazeLayout.json in the core module
 * for an example.
 */
public class MazeLayoutManager {
    public final List<MazeLayout> mazeLayouts;

    public MazeLayoutManager() {
        this.mazeLayouts = new ArrayList<>();
    }

    /**
     * This loads in the specified JSON asset. The assetName parameter should be the name of the JSON file you created.
     * The layout is added to the mazeLayouts list.
     * @param assetName JSON asset name
     */
    public void load(String assetName) {
        final Set<ResourceUrn> configUrns = Assets.getAssetHelper().listAssets(Json.class, assetName);
        for (ResourceUrn configUrn : configUrns) {
            JSONObject rootNode = Validator.getValidatedJSON(configUrn.toString(), "engine:schemaMazeLayouts");
            String layoutName = "";
            ArrayList<ArrayList<Boolean>> inners = new ArrayList<>();
            for (String s : rootNode.keySet()) {
                if (!(rootNode.get(s) instanceof JSONArray)) {
                    layoutName = rootNode.getString(s);
                    continue;
                }
                JSONArray mazeNode = rootNode.getJSONArray(s);
                ArrayList<Object> arrayList = new ArrayList<>(mazeNode.toList());
                if (s.equals("inner")) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        inners.add((ArrayList<Boolean>) arrayList.get(i));
                    }
                }
            }
            boolean[][] innersArray = new boolean[inners.size()][];
            for (int i = 0; i < inners.size(); i++) {
                ArrayList<Boolean> row = inners.get(i);
                Boolean[] rowObj = row.toArray(new Boolean[row.size()]);
                boolean[] rowPrim = new boolean[rowObj.length];
                for (int j = 0; j < rowObj.length; j++) {
                    rowPrim[j] = rowObj[j];
                }
                innersArray[i] = rowPrim;
            }
            mazeLayouts. add(new MazeLayout(innersArray, new boolean[0][], new boolean[0][], new boolean[0][], layoutName));
        }
    }

    public List<MazeLayout> getMazeLayouts() {
        return mazeLayouts;
    }

    /**
     * Return layout with the specified name
     * @param name name of the layout. Specified by the name property in JSON
     * @return the MazeLayout
     */
    public MazeLayout getLayout(String name) {
        for (MazeLayout layout : mazeLayouts) {
            if (layout.getName().equals(name)) {
                return layout;
            }
        }
        return null;
    }
}
