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
package org.destinationsol.game.planet;

import com.badlogic.gdx.graphics.Color;
import org.json.JSONObject;
import org.destinationsol.game.GameColors;

public class SkyConfig {
    public final Color dawn;
    public final Color day;

    public SkyConfig(Color dawnHsba, Color dayHsba) {
        this.dawn = dawnHsba;
        this.day = dayHsba;
    }

    public static SkyConfig load(JSONObject skyNode, GameColors cols) {
        if (skyNode == null) {
            return null;
        }
        Color dawn = cols.load(skyNode.getString("dawnColor"));
        Color day = cols.load(skyNode.getString("dayColor"));
        return new SkyConfig(dawn, day);
    }
}
