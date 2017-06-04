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
package org.destinationsol.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.planet.SolSystem;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.UiDrawer;

public class ZoneNameAnnouncer {
    private static final float FADE_TIME = 4f;
    private final Color myCol = new Color(1, 1, 1, 1);
    private String myZone;
    private String myText;

    public void update(SolGame game) {
        PlanetManager pm = game.getPlanetMan();
        String zone = null;
        String pref = null;
        Vector2 camPos = game.getCam().getPos();
        Planet p = pm.getNearestPlanet();
        if (p.getPos().dst(camPos) < p.getFullHeight()) {
            zone = p.getName();
            pref = "Planet";
        } else {
            SolSystem s = pm.getNearestSystem(camPos);
            if (s.getPos().dst(camPos) < s.getRadius()) {
                zone = s.getName();
                pref = "System";
            }
        }
        boolean reset = zone != null && !zone.equals(myZone);
        myZone = zone;
        if (reset) {
            myCol.a = 1f;
            myText = pref + ":\n" + myZone;
        } else if (myCol.a > 0) {
            myCol.a -= Const.REAL_TIME_STEP / FADE_TIME;
        }
    }

    public void drawText(UiDrawer uiDrawer) {
        if (myCol.a <= 0) {
            return;
        }
        uiDrawer.drawString(myText, uiDrawer.r / 2, .15f, FontSize.MENU * 1.5f, true, myCol);
    }
}
