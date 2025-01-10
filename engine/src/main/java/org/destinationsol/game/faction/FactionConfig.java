/*
 * Copyright 2025 The Terasology Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.faction;

import com.badlogic.gdx.graphics.Color;
import org.json.JSONArray;
import org.json.JSONObject;
import org.terasology.gestalt.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FactionConfig {
    private FactionConfig() {
    }

    public static Faction load(ResourceUrn id, JSONObject jsonObject) {
        Color gdxColour = Color.valueOf(jsonObject.getString("colour"));
        List<ResourceUrn> shipDesigns = new ArrayList<>();
        JSONArray shipDesignsArray = jsonObject.getJSONArray("shipDesigns");
        for (int designNo = 0; designNo < shipDesignsArray.length(); designNo++) {
            shipDesigns.add(new ResourceUrn(shipDesignsArray.getString(designNo)));
        }
        Map<String, Integer> reputationImpacts = new HashMap<>();
        return new Faction(id, jsonObject.getString("name"),
                jsonObject.getString("description"),
                new org.terasology.nui.Color(gdxColour.r, gdxColour.g, gdxColour.b, gdxColour.a),
                jsonObject.optInt("defaultDisposition", 0), shipDesigns, reputationImpacts);
    }
}
