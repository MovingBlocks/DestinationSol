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
    private final Rectangle myWarn;
    private final Color myBgCol;
    private final Color myTextCol;
    private final float myBgOrigA;
    private final String myText;

    float drawPerc;

    WarnDrawer(float resolutionRatio, String text) {
        myWarn = rect(resolutionRatio);
        myText = text;
        myBgCol = new Color(SolColor.UI_WARN);
        myBgOrigA = myBgCol.a;
        myTextCol = new Color(SolColor.WHITE);
    }

    private static Rectangle rect(float resolutionRatio) {
        return new Rectangle(.4f * resolutionRatio, 0, .2f * resolutionRatio, .1f);
    }

    public void update(SolGame game) {
        if (shouldWarn(game)) {
            drawPerc = 1;
        } else {
            drawPerc = SolMath.approach(drawPerc, 0, Const.REAL_TIME_STEP / FADE_TIME);
        }
        myBgCol.a = myBgOrigA * drawPerc;
        myTextCol.a = drawPerc;
    }

    protected abstract boolean shouldWarn(SolGame game);

    public void draw(UiDrawer uiDrawer) {
        uiDrawer.draw(myWarn, myBgCol);
    }

    public void drawText(UiDrawer uiDrawer) {
        uiDrawer.drawString(myText, myWarn.x + myWarn.width / 2, myWarn.y + myWarn.height / 2, FontSize.MENU, true, myTextCol);
    }
}
