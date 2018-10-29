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
package org.destinationsol.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.CommonDrawer;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;
import org.destinationsol.ui.responsiveUi.UiAbsolutePosition;
import org.destinationsol.ui.responsiveUi.UiPosition;

public class UiDrawer implements ResizeSubscriber {
    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    public static final float FONT_SIZE_PX = 19f;

    private Matrix4 straightMtx;
    private final float uiLineWidth;

    public static float fontSize;
    public static final TextureRegion whiteTexture = Assets.getAtlasRegion("engine:uiWhiteTex");
    public Rectangle filler;
    private final CommonDrawer drawer;

    private DisplayDimensions displayDimensions;

    public static UiPosition UI_POSITION_TOP;
    public static UiPosition UI_POSITION_TOP_RIGHT;
    public static UiPosition UI_POSITION_RIGHT;
    public static UiPosition UI_POSITION_BOTTOM_RIGHT;
    public static UiPosition UI_POSITION_BOTTOM;
    public static UiPosition UI_POSITION_BOTTOM_LEFT;
    public static UiPosition UI_POSITION_LEFT;
    public static UiPosition UI_POSITION_TOP_LEFT;
    public static UiPosition UI_POSITION_CENTER;

    public UiDrawer(CommonDrawer commonDrawer) {
        displayDimensions = SolApplication.displayDimensions;
        drawer = commonDrawer;

        uiLineWidth = 1.0f / displayDimensions.getHeight();

        recomputeStraightMtx();
        recomputeFontSize();
        drawer.setMatrix(straightMtx);

        filler = new Rectangle(0, 0, displayDimensions.getRatio(), 1);

        UI_POSITION_TOP = new UiAbsolutePosition(0.5f, 0);
        UI_POSITION_TOP_RIGHT = new UiAbsolutePosition(1, 0);
        UI_POSITION_RIGHT = new UiAbsolutePosition(1, 0.5f);
        UI_POSITION_BOTTOM_RIGHT = new UiAbsolutePosition(1, 1);
        UI_POSITION_BOTTOM = new UiAbsolutePosition(0.5f, 1);
        UI_POSITION_BOTTOM_LEFT = new UiAbsolutePosition(0, 1);
        UI_POSITION_LEFT = new UiAbsolutePosition(0, 0.5f);
        UI_POSITION_TOP_LEFT = new UiAbsolutePosition(0, 0);
        UI_POSITION_CENTER = new UiAbsolutePosition(0.5f, 0.5f);

        SolApplication.addResizeSubscriber(this);
    }

    public void updateMtx() {
        drawer.setMatrix(straightMtx);
    }

    public void drawString(String s, float x, float y, float scale, boolean centered, Color tint) {
        drawString(s, x, y, scale, TextAlignment.CENTER, centered, tint);
    }

    public void drawString(String s, float x, float y, float scale, TextAlignment align, boolean centered, Color tint) {
        drawer.drawString(s, x, y, scale * fontSize, align, centered, tint);
    }

    /**
     * Returns the visible length of a string when drawn.
     *
     * @param s The string to measure
     * @param scale The scale of the string
     * @return The visible length
     */
    public int getStringLength(String s, float scale) {
        final GlyphLayout layout = drawer.makeFontLayout(s, scale * fontSize);
        return (int) (layout.width * displayDimensions.getHeight());
    }

    public int getStringHeight(String s, float scale) {
        final GlyphLayout layout = drawer.makeFontLayout(s, scale * fontSize);
        return (int) (layout.height * displayDimensions.getHeight());
    }

    public void draw(TextureRegion tr, float width, float height, float origX, float origY, float x, float y, float rot, Color tint) {
        drawer.draw(tr, width, height, origX, origY, x, y, rot, tint);
    }

    public void draw(Rectangle rect, Color tint) {
        drawer.draw(whiteTexture, rect, tint);
    }

    public void drawCircle(Vector2 center, float radius, Color col) {
        drawer.drawCircle(whiteTexture, center, radius, col, uiLineWidth, 1);
    }

    public void drawLine(float x, float y, float angle, float len, Color col) {
        drawer.drawLine(whiteTexture, x, y, angle, len, col, uiLineWidth);
    }

    public void drawLine(Vector2 p1, Vector2 p2, Color col) {
        drawer.drawLine(whiteTexture, p1, p2, col, uiLineWidth, false);
    }

    @Override
    public void resize() {
        recomputeStraightMtx();
        recomputeFontSize();
        filler = new Rectangle(0, 0, displayDimensions.getRatio(), 1);
    }

    private void recomputeStraightMtx() {
        straightMtx = new Matrix4().setToOrtho2D(0, 1, displayDimensions.getRatio(), -1);
    }
    private void recomputeFontSize() {
        fontSize = FONT_SIZE_PX / displayDimensions.getHeight();
    }
}
