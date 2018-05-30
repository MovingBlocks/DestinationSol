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
package org.destinationsol.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.SolUiControl;
import org.destinationsol.ui.SolUiScreen;
import org.destinationsol.ui.UiDrawer;

import java.util.Collections;
import java.util.List;

public class Console implements SolUiScreen {
    private static final Vector2 TOP_LEFT = new Vector2(0.03f, 0.03f);
    private static final Vector2 BOTTOM_RIGHT = new Vector2(0.5f, 0.5f);
    private static final float FRAME_WIDTH = 0.02f;
    private final BitmapFont font;

    public Console() {
        font = Assets.getFont("engine:main").getBitmapFont();
    }

    @Override
    public List<SolUiControl> getControls() {
        return Collections.emptyList();
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        uiDrawer.draw(new Rectangle(TOP_LEFT.x, TOP_LEFT.y,
                (BOTTOM_RIGHT.x - TOP_LEFT.x) * uiDrawer.r, BOTTOM_RIGHT.y - TOP_LEFT.y),
                SolColor.UI_LIGHT);
        uiDrawer.draw(new Rectangle(TOP_LEFT.x + FRAME_WIDTH,
                TOP_LEFT.y + FRAME_WIDTH,
                (BOTTOM_RIGHT.x - TOP_LEFT.x) * uiDrawer.r - 2 * FRAME_WIDTH,
                BOTTOM_RIGHT.y - TOP_LEFT.y - 2 * FRAME_WIDTH),
                SolColor.UI_BG_LIGHT);
        uiDrawer.draw(new Rectangle(TOP_LEFT.x + 2 * FRAME_WIDTH,
                TOP_LEFT.y + 2 * FRAME_WIDTH,
                (BOTTOM_RIGHT.x - TOP_LEFT.x) * uiDrawer.r - 4 * FRAME_WIDTH,
                BOTTOM_RIGHT.y - TOP_LEFT.y - 4 * FRAME_WIDTH),
                SolColor.UI_BG_LIGHT);
    }

    @Override
    public void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        final String s = "TOTO je hodne dlouhy texttextetexttexttexftfextjh";
        int x = 0;
        for (char c : s.toCharArray()) {
            x += font.getData().getGlyph(c).width;
        }
        System.out.println(x);
        uiDrawer.drawString(s, TOP_LEFT.x + 2 * FRAME_WIDTH, TOP_LEFT.y + 2 * FRAME_WIDTH, 0.5f, UiDrawer.TextAlignment.LEFT, false, Color.WHITE);
    }
}
