/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game.maze;

import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.TextureManager;
import org.destinationsol.assets.Assets;
import org.destinationsol.assets.json.Json;
import org.destinationsol.files.HullConfigManager;
import org.destinationsol.game.item.ItemManager;
import org.terasology.assets.ResourceUrn;

import java.util.ArrayList;
import java.util.List;

public class MazeConfigs {
    public final List<MazeConfig> configs;

    public MazeConfigs(TextureManager textureManager, HullConfigManager hullConfigs, ItemManager itemManager) {
        configs = new ArrayList<>();

        Json json = Assets.getJson(new ResourceUrn("core:mazesConfig"));
        JsonValue rootNode = json.getJsonValue();

        for (JsonValue mazeNode : rootNode) {
            MazeConfig c = MazeConfig.load(textureManager, hullConfigs, mazeNode, itemManager);
            configs.add(c);
        }

        json.dispose();
    }
}
