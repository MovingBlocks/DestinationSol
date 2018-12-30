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
package org.destinationsol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import org.destinationsol.game.SolGame;
import org.destinationsol.game.ObjectManager;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolObject;
import org.destinationsol.game.FactionInfo;
import org.destinationsol.game.ship.SolShip;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.UiDrawer;

/**
 * Acquires faction information fromm all the ships and draws it above them.
 */
public class FactionDisplay {
    private SolCam cam;
    private FactionInfo info;
    private boolean isPressed = false;

    public FactionDisplay(SolGame game, FactionInfo factionInfo) {
        cam = game.getCam();
        info = factionInfo;
    }

    public void drawFactionNames(SolGame game, UiDrawer uiDrawer, SolInputManager inputManager, ObjectManager objManager) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            isPressed = !isPressed;
        }
        // angle must be zero as the camera angles on planets mess up the text display
        if (isPressed && cam.getAngle() == 0 && !inputManager.isScreenOn(game.getScreens().mapScreen)) {
            for(SolObject obj: objManager.getObjects()) {
                if (obj instanceof SolShip) {
                    SolShip ship = (SolShip) obj;
                    Vector2 drawPosition = cam.worldToScreen(ship);
                    uiDrawer.drawString(ship.getFactionName(), drawPosition.x * SolApplication.displayDimensions.getRatio(),
                            drawPosition.y -.1f, 1, false, Color.valueOf(info.getFactionColors().get(ship.getFactionID()).toString()));
                }
            }
        }
    }
}
