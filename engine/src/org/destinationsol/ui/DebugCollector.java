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
package org.destinationsol.ui;

import com.badlogic.gdx.utils.TimeUtils;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.DebugOptions;
import org.destinationsol.game.screens.BorderDrawer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DebugCollector {
    private static final long WARN_TIME = 6000;
    private static final StringBuilder myDebugStrings = new StringBuilder();
    private static final Map<String, Long> myWarnings = new HashMap<>();

    private DebugCollector() {
    }

    public static void draw(UiDrawer drawer) {
        drawer.drawString(myDebugStrings.toString(), .5f, BorderDrawer.TISHCH_SZ, FontSize.DEBUG, false, SolColor.WHITE);
    }

    public static void debug(String name, String val) {
        myDebugStrings.append(name).append(": ").append(val).append("\n");
    }

    public static void debug(String name, int val) {
        myDebugStrings.append(name).append(": ").append(val).append("\n");
    }

    public static void warn(String msg) {
        if (!DebugOptions.SHOW_WARNINGS) {
            return;
        }
        myWarnings.put(msg, TimeUtils.millis() + WARN_TIME);
    }

    public static void update() {
        myDebugStrings.setLength(0);

        Iterator<Map.Entry<String, Long>> it = myWarnings.entrySet().iterator();
        long now = TimeUtils.millis();
        while (it.hasNext()) {
            Map.Entry<String, Long> e = it.next();
            if (e.getValue() < now) {
                it.remove();
                continue;
            }
            myDebugStrings.append(e.getKey()).append("\n");
        }

    }

}
