/*
 * Copyright 2017 MovingBlocks
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
import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.common.SolColor;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.UiDrawer;

public class ThrottleDrawer {
    private final Rectangle progressBarRect;
    private final Color emptyProgressBarColor;
    private final Color fullProgressBarColor;

    public ThrottleDrawer(float resolutionRatio) {
        progressBarRect = new Rectangle(.4f * resolutionRatio, 0.9f, .2f * resolutionRatio, .03f);
        emptyProgressBarColor = SolColor.UI_DARK;
        fullProgressBarColor = SolColor.UI_LIGHT;
    }

    public void draw(UiDrawer uiDrawer, Pilot pilot) {
        float throttle = pilot.getThrottle();

        uiDrawer.draw(uiDrawer.whiteTexture, progressBarRect.width, progressBarRect.height,
                0, 0, progressBarRect.x, progressBarRect.y, 0, emptyProgressBarColor);
        uiDrawer.draw(uiDrawer.whiteTexture, progressBarRect.width * throttle, progressBarRect.height,
                0, 0, progressBarRect.x, progressBarRect.y, 0, fullProgressBarColor);

    }

    public void drawText(UiDrawer uiDrawer, Pilot pilot) {
        String tooltipText = "Throttle";

        float tooltipX = progressBarRect.x + progressBarRect.width / 2;
        float tooltipY = progressBarRect.y + progressBarRect.height + 0.01f;

        uiDrawer.drawString(tooltipText, tooltipX, tooltipY, FontSize.HUD,
                UiDrawer.TextAlignment.CENTER, false, SolColor.WHITE);

        String throttlePercentText = (int) (pilot.getThrottle() * 100) + "%";

        float throttleTextCenterX = progressBarRect.x + progressBarRect.width / 2;
        float throttleTextCenterY = progressBarRect.y + progressBarRect.height / 2;

        uiDrawer.drawString(throttlePercentText, throttleTextCenterX, throttleTextCenterY, FontSize.HUD, true, SolColor.W50);
    }
}
