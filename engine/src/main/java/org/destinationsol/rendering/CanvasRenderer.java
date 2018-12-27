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
package org.destinationsol.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolMath;
import org.destinationsol.ui.UiDrawer;

public interface CanvasRenderer {


    void setMatrix(Matrix4 matrix);
    void begin();
    void end();
    void drawString(String s, float x, float y, float fontSize, boolean centered, Color col);

    void drawString(String s, float x, float y, float fontSize, UiDrawer.TextAlignment align, boolean verticalCentering, Color col);

    float getLineSpacing(float fontSize) ;
    /**
     * Creates a GlyphLayout for a provided string, using the engine font.
     *
     * @param s The provided string.
     * @param fontSize The size of the font.
     * @param col The color of the font.
     * @return The final GlyphLayout.
     */
    GlyphLayout makeFontLayout(String s, float fontSize, Color col);
    /**
     * Creates a GlyphLayout for a provided string, using the engine font.
     *
     * @param s The provided string.
     * @param fontSize The size of the font.
     * @return The final GlyphLayout.
     */
    GlyphLayout makeFontLayout(String s, float fontSize);

    void draw(TextureRegion tr, float width, float height, float origX, float origY, float x, float y, float rot, Color tint);

    void draw(TextureRegion tex, Rectangle rect, Color tint);

    void drawCircle(TextureRegion tex, Vector2 center, float radius, Color col, float width, float vh);

    void drawLine(TextureRegion tex, float x, float y, float angle, float len, Color col, float width);

    void drawLine(TextureRegion tex, Vector2 startPoint, Vector2 endPoint, Color color, float width, boolean precise);

    void dispose();

}
