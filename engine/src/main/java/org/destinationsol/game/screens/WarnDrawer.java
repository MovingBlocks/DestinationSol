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
import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.Const;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.UiDrawer;

public abstract class WarnDrawer {
    private static final float FADE_TIME = 1f;

    float drawPercentage;

    private final Rectangle warningRectangle;
    private final Color backgroundColor;
    private final Color textColor;
    private final float backgroundOriginA;
    private final String text;


    WarnDrawer(float resolutionRatio, String text) {
        warningRectangle = rect(resolutionRatio);
        this.text = text;
        backgroundColor = new Color(SolColor.UI_WARN);
        backgroundOriginA = backgroundColor.a;
        textColor = new Color(SolColor.WHITE);
    }

    private static Rectangle rect(float resolutionRatio) {
        return new Rectangle(.4f * resolutionRatio, 0, .2f * resolutionRatio, .1f);
    }

    public void update(SolGame game) {
        if (shouldWarn(game)) {
            drawPercentage = 1;
        } else {
            drawPercentage = SolMath.approach(drawPercentage, 0, Const.REAL_TIME_STEP / FADE_TIME);
        }
        backgroundColor.a = backgroundOriginA * drawPercentage;
        textColor.a = drawPercentage;
    }

    protected abstract boolean shouldWarn(SolGame game);

    public void draw(UiDrawer uiDrawer) {
        uiDrawer.draw(warningRectangle, backgroundColor);
    }

    public void drawText(UiDrawer uiDrawer) {
        uiDrawer.drawString(text, warningRectangle.x + warningRectangle.width / 2, warningRectangle.y + warningRectangle.height / 2, FontSize.MENU, true, textColor);
    }
}
