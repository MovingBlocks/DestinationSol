/*
 * Copyright 2015 MovingBlocks
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

package org.destinationsol.game;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.destinationsol.common.SolColorUtil;
import org.destinationsol.files.FileManager;

import java.util.HashMap;

public class GameColors {

    public final Color fire;
    public final Color smoke;
    public final Color hullLights;

    private final HashMap<String, Color> colors;

    public GameColors() {
        JsonReader r = new JsonReader();
        FileHandle configFile = FileManager.getInstance().getConfigDirectory().child("colors.json");
        JsonValue node = r.parse(configFile);
        colors = new HashMap<String, Color>();

        for (JsonValue colVal : node) {
            Color c = load(colVal.asString());
            colors.put(colVal.name, c);
        }

        fire = get("fire");
        smoke = get("smoke");
        hullLights = get("hullLights");
    }

    public Color get(String name) {
        Color result = colors.get(name);

        if (result == null) {
            throw new AssertionError("Color " + name + " is not defined.");
        }

        return result;
    }

    public Color load(String s) {
        if (s.contains(" ")) {
            return SolColorUtil.load(s);
        }

        return get(s);
    }
}
