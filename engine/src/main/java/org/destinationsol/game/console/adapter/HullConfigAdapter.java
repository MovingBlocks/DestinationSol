/*
 * Copyright 2019 MovingBlocks
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
package org.destinationsol.game.console.adapter;

import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.game.ship.hulls.HullConfig;
import org.terasology.gestalt.assets.ResourceUrn;

public class HullConfigAdapter implements ParameterAdapter<HullConfig> {

    private final SolApplication application;

    public HullConfigAdapter(SolApplication application) {
        this.application = application;
    }

    @Override
    public HullConfig parse(String raw) {
        for (ResourceUrn urn : Assets.getAssetHelper().list(Json.class)) {
            if ((urn.getModuleName() + ":" + urn.getResourceName()).equals(raw)) {
                try {
                    HullConfig config = application.getGame().getHullConfigManager().getConfig(raw);
                    if (config.getType() != HullConfig.Type.STATION) {
                        return config;
                    }
                } catch (RuntimeException e) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public String convertToString(HullConfig value) {
        return value.getInternalName();
    }
}
