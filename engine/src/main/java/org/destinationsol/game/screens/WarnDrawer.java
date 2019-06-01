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
import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.Const;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.SolGame;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;
import java.util.List;

public abstract class WarnDrawer {
    private static final float FADE_TIME = 1f;
    private final Color backgroundColor;
    private final Color textColor;
    private final float backgroundOriginA;
    private final String text;
    private final DisplayDimensions displayDimensions;
    private final List<Rectangle> rectangles = new ArrayList<>();

    float drawPercentage;

    public WarnDrawer(String text) {
        this(text, SolColor.UI_WARN);
    }

    public WarnDrawer(String text, Color backgroundColor) {
        displayDimensions = SolApplication.displayDimensions;

        this.text = text;
        this.backgroundColor = new Color(backgroundColor);
        backgroundOriginA = backgroundColor.a;
        textColor = new Color(SolColor.WHITE);
        // create the 3 rectangles where notifications can appear
        for(int i=0; i<3; i++) {
            rectangles.add(createRectangle(i));
        }
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

    public void draw(UiDrawer uiDrawer, int drawIndex) {
        if(drawIndex >= rectangles.size()) return;
        uiDrawer.draw(rectangles.get(drawIndex), backgroundColor);
    }

    public void drawText(UiDrawer uiDrawer, int drawIndex) {
        if(drawIndex >= rectangles.size()) return;
        Rectangle warningRectangle = rectangles.get(drawIndex);
        uiDrawer.drawString(text, warningRectangle.x + warningRectangle.width / 2.f, warningRectangle.y + warningRectangle.height / 2.f, FontSize.MENU, true, textColor);
    }

    /**
     * Create background rectangle by calculating bounds
     * @param drawIndex where the rectangle starts on the screen
     */
    private Rectangle createRectangle(int drawIndex) {
        float x;
        float y = 0.05f;
        switch(drawIndex) {
            case 1: // left of center
                x = 0.18f * displayDimensions.getRatio();
                break;
            case 2: // right of center
                x = 0.62f * displayDimensions.getRatio();
                break;
            case 0: // fallthrough to default intended
            default:
                x = 0.4f * displayDimensions.getRatio();
        }
        return new Rectangle(x, y, .2f * displayDimensions.getRatio(), .1f);
    }
}
