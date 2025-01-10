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

import org.destinationsol.assets.AssetHelper;
import org.destinationsol.assets.json.Json;
import org.destinationsol.assets.json.Validator;
import org.json.JSONObject;
import org.terasology.gestalt.assets.ResourceUrn;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class FactionsConfigs {
    public final Map<ResourceUrn, Faction> factionConfigs;

    @Inject
    public FactionsConfigs(AssetHelper assetHelper) {
        factionConfigs = new HashMap<>();

        for (ResourceUrn resource : assetHelper.listAssets(Json.class, "factions")) {
            JSONObject rootNode = Validator.getValidatedJSON(resource.toString(), "engine:schemaFactions");

            Map<ResourceUrn, Map<ResourceUrn, Integer>> relations = new HashMap<>();

            for (String factionName : rootNode.keySet()) {
                ResourceUrn normalisedName = new ResourceUrn(factionName);
                if (!factionConfigs.containsKey(normalisedName)) {
                    factionConfigs.put(normalisedName, FactionConfig.load(normalisedName, assetHelper.get(normalisedName, Json.class).get().getJsonValue()));
                }
                Map<ResourceUrn, Integer> factionRelations = new HashMap<>();
                relations.put(normalisedName, new HashMap<>());
                JSONObject factionAttributes = rootNode.getJSONObject(factionName);
                if (factionAttributes.has("relations")) {
                    JSONObject factionRelationsJSON = factionAttributes.getJSONObject("relations");
                    for (String key : factionRelationsJSON.keySet()) {
                        factionRelations.put(new ResourceUrn(key), factionRelationsJSON.getInt(key));
                    }
                }
                relations.put(normalisedName, factionRelations);
            }

            for (Map.Entry<ResourceUrn, Map<ResourceUrn, Integer>> factionRelations : relations.entrySet()) {
                Faction faction = factionConfigs.get(factionRelations.getKey());
                for (Map.Entry<ResourceUrn, Integer> factionRelation : factionRelations.getValue().entrySet()) {
                    Faction otherFaction = factionConfigs.get(factionRelation.getKey());
                    faction.setRelation(otherFaction, factionRelation.getValue());
                    if (!otherFaction.isAwareOf(faction)) {
                        otherFaction.setRelation(faction, factionRelation.getValue());
                    }
                }
            }
        }
    }
}
