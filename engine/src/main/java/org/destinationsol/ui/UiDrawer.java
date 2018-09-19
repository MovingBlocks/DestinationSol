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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.CommonDrawer;
import org.destinationsol.SolApplication;
import org.destinationsol.assets.Assets;

import java.util.HashMap;
import java.util.Map;

public class UiDrawer implements ResizeSubscriber {
    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    private static final float FONT_SIZE = .02f;

    private Matrix4 straightMtx;
    private final float uiLineWidth;

    public static final TextureRegion whiteTexture = Assets.getAtlasRegion("engine:uiWhiteTex");
    public final Rectangle filler;
    private final CommonDrawer drawer;
    private Boolean isTextMode;

    private DisplayDimensions displayDimensions;

    public static Map<String, Position> positions = new HashMap<>();

    public UiDrawer(CommonDrawer commonDrawer) {
        displayDimensions = SolApplication.displayDimensions;
        drawer = commonDrawer;

        uiLineWidth = 1.0f / displayDimensions.getHeight();

        recomputeStraightMtx();
        drawer.setMatrix(straightMtx);

        filler = new Rectangle(0, 0, displayDimensions.getRatio(), 1);

        positions.put("top", new Position(0.5f, 0));
        positions.put("topRight", new Position(1, 0));
        positions.put("right", new Position(1, 0.5f));
        positions.put("bottomRight", new Position(1, 1));
        positions.put("bottom", new Position(0.5f, 1));
        positions.put("bottomLeft", new Position(0, 1));
        positions.put("left", new Position(0, 0.5f));
        positions.put("topLeft", new Position(0, 0));
        positions.put("center", new Position(0.5f, 0.5f));

        SolApplication.addResizeSubscriber(this);
    }

    public void updateMtx() {
        drawer.setMatrix(straightMtx);
    }

    public void drawString(String s, float x, float y, float scale, boolean centered, Color tint) {
        drawString(s, x, y, scale, TextAlignment.CENTER, centered, tint);
    }

    public void drawString(String s, float x, float y, float scale, TextAlignment align, boolean centered, Color tint) {
        if (isTextMode != null && !isTextMode) {
            throw new AssertionError("drawing text in atlas mode");
        }
        drawer.drawString(s, x, y, scale * FONT_SIZE, align, centered, tint);
    }

    private void check() {
        if (isTextMode != null && isTextMode) {
            throw new AssertionError("drawing atlas in text mode");
        }
    }

    public void draw(TextureRegion tr, float width, float height, float origX, float origY, float x, float y, float rot, Color tint) {
        check();
        drawer.draw(tr, width, height, origX, origY, x, y, rot, tint);
    }

    public void draw(Rectangle rect, Color tint) {
        check();
        drawer.draw(whiteTexture, rect, tint);
    }

    public void drawCircle(Vector2 center, float radius, Color col) {
        check();
        drawer.drawCircle(whiteTexture, center, radius, col, uiLineWidth, 1);
    }

    public void drawLine(float x, float y, float angle, float len, Color col) {
        check();
        drawer.drawLine(whiteTexture, x, y, angle, len, col, uiLineWidth);
    }

    public void drawLine(Vector2 p1, Vector2 p2, Color col) {
        check();
        drawer.drawLine(whiteTexture, p1, p2, col, uiLineWidth, false);
    }

    public void setTextMode(Boolean textMode) {
        isTextMode = textMode;
    }

    @Override
    public void resize() {
        recomputeStraightMtx();
    }

    private void recomputeStraightMtx() {
        straightMtx = new Matrix4().setToOrtho2D(0, 1, displayDimensions.getRatio(), -1);
    }
}
