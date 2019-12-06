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
package org.destinationsol.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.SolApplication;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.planet.Planet;
import org.destinationsol.game.planet.PlanetManager;
import org.destinationsol.game.planet.SolSystem;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.UiDrawer;

public class ZoneNameAnnouncer {
    private DisplayDimensions displayDimensions;

    private static final float FADE_TIME = 4f;
    private final Color color = new Color(1, 1, 1, 1);
    private String zone;
    private String text;

    ZoneNameAnnouncer() {
        displayDimensions = SolApplication.displayDimensions;
    }

    public void update(SolGame game) {
        PlanetManager planetManager = game.getPlanetManager();
        String currentZone = null;
        String pref = null;
        Vector2 camPosition = game.getCam().getPosition();
        Planet planet = planetManager.getNearestPlanet();
        if (planet.getPosition().dst(camPosition) < planet.getFullHeight()) {
            currentZone = planet.getName();
            pref = "Planet";
        } else {
            SolSystem system = planetManager.getNearestSystem(camPosition);
            if (system.getPosition().dst(camPosition) < system.getRadius()) {
                currentZone = system.getName();
                pref = "System";
            }
        }
        boolean reset = currentZone != null && !currentZone.equals(this.zone);
        this.zone = currentZone;
        if (reset) {
            color.a = 1f;
            text = pref + ":\n" + this.zone;
        } else if (color.a > 0) {
            color.a -= Const.REAL_TIME_STEP / FADE_TIME;
        }
    }

    public void drawText(UiDrawer uiDrawer) {
        if (color.a <= 0) {
            return;
        }
        uiDrawer.drawString(text, displayDimensions.getRatio() / 2, .15f, FontSize.MENU * 1.5f, true, color);
    }
}
