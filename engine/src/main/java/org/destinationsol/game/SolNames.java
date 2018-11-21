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

import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.terasology.assets.ResourceUrn;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

public class SolNames {
    public final HashMap<String, ArrayList<String>> planets;
    public final ArrayList<String> systems;

    public SolNames() {
        planets = new HashMap<String, ArrayList<String>>();
        systems = new ArrayList<String>();

        final Set<ResourceUrn> planetNameConfigs = Assets.getAssetHelper().list(Json.class, "[a-zA-Z0-9]*:planetNamesConfig");
        for (ResourceUrn planetNameConfig : planetNameConfigs) {
            planets.put(planetNameConfig.getModuleName().toString(), readList(planetNameConfig.toString()));
        }

        final Set<ResourceUrn> systemNameConfigs = Assets.getAssetHelper().list(Json.class, "[a-zA-Z0-9]*:systemNamesConfig");
        for (ResourceUrn systemNameConfig : systemNameConfigs) {
            systems.addAll(readList(systemNameConfig.toString()));
        }
    }

    private ArrayList<String> readList(String fileName) {
        Json json = Assets.getJson(fileName);
        JsonValue rootNode = json.getJsonValue();

        ArrayList<String> list = new ArrayList<>();
        for (JsonValue node : rootNode) {
            list.add(node.name());
        }

        json.dispose();

        return list;
    }
}
