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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolMath;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.ResizeSubscriber;
import org.destinationsol.ui.UiDrawer;

public class CommonDrawer implements ResizeSubscriber {
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final float originalFontHeight;
    private final GlyphLayout layout;
    private final OrthographicCamera orthographicCamera;
    private final Viewport screenViewport;

    private DisplayDimensions displayDimensions;
    public final TextureAtlas.AtlasRegion debugWhiteTexture;

    CommonDrawer() {
        displayDimensions = SolApplication.displayDimensions;

        spriteBatch = new SpriteBatch();

        debugWhiteTexture = Assets.getAtlasRegion("engine:uiWhiteTex");
        font = Assets.getFont("engine:main").getBitmapFont();
        originalFontHeight = font.getXHeight();

        layout = new GlyphLayout();

        orthographicCamera = new OrthographicCamera(1024, 768);
        screenViewport = new ScreenViewport(orthographicCamera);

        SolApplication.addResizeSubscriber(this);
    }

    public void setMatrix(Matrix4 matrix) {
        spriteBatch.setProjectionMatrix(matrix);
    }

    public void begin() {
        orthographicCamera.update();
        spriteBatch.begin();
    }

    public void end() {
        spriteBatch.end();
    }

    public void drawString(String s, float x, float y, float fontSize, boolean centered, Color col, boolean additive) {
        setAdditive(additive);
        drawString(s, x, y, fontSize, UiDrawer.TextAlignment.CENTER, centered, col);
    }

    public void drawString(String s, float x, float y, float fontSize, UiDrawer.TextAlignment align, boolean verticalCentering, Color col) {
        if (s == null) {
            return;
        }

        makeFontLayout(s, fontSize, col);

        switch (align) {
            case LEFT:
                break;
            case CENTER:
                x -= layout.width / 2;
                break;
            case RIGHT:
                x -= layout.width;
                break;
        }

        if (verticalCentering) {
            y -= layout.height / 2;
        }

        font.draw(spriteBatch, layout, x, y);
    }

    public float getLineSpacing(float fontSize) {
        font.getData().setScale(fontSize / originalFontHeight);
        return font.getLineHeight();
    }

    /**
     * Creates a GlyphLayout for a provided string, using the engine font.
     *
     * @param s The provided string.
     * @param fontSize The size of the font.
     * @param col The color of the font.
     * @return The final GlyphLayout.
     */
    public GlyphLayout makeFontLayout(String s, float fontSize, Color col){
        font.setColor(col);
        return makeFontLayout(s, fontSize);
    }

    /**
     * Creates a GlyphLayout for a provided string, using the engine font.
     *
     * @param s The provided string.
     * @param fontSize The size of the font.
     * @return The final GlyphLayout.
     */
    public GlyphLayout makeFontLayout(String s, float fontSize){
        font.getData().setScale(fontSize / originalFontHeight);
        // http://www.badlogicgames.com/wordpress/?p=3658
        layout.reset();
        layout.setText(font, s);
        return layout; // returns for width calculations
    }

    public void draw(TextureRegion tr, float width, float height, float origX, float origY, float x, float y,
                     float rot, Color tint,boolean additive) {
        setAdditive(additive);
        setTint(tint);
        spriteBatch.draw(tr, x - origX, y - origY, origX, origY, width, height, 1, 1, rot);
//        setTint(Color.CYAN);
//        spriteBatch.draw(UiDrawer.whiteTexture, 0, 0, 0.5f, 0.5f); // debug rectangle for render overhaul purpose
    }

    private void setTint(Color tint) {
        spriteBatch.setColor(tint);
    }

    public void draw(TextureRegion tex, Rectangle rect, Color tint,boolean addative) {
        draw(tex, rect.width, rect.height, (float) 0, (float) 0, rect.x, rect.y, (float) 0, tint,addative);
    }

    public void drawCircle(TextureRegion tex, Vector2 center, float radius, Color col, float width, float vh,boolean addative) {
        float relRad = radius / vh;
        int pointCount = (int) (160 * relRad);
        Vector2 position = SolMath.getVec();
        if (pointCount < 8) {
            pointCount = 8;
        }
        float lineLen = radius * MathUtils.PI * 2 / pointCount;
        float angleStep = 360f / pointCount;
        float angleStepH = angleStep / 2;
        for (int i = 0; i < pointCount; i++) {
            float angle = angleStep * i;
            SolMath.fromAl(position, angle, radius);
            position.add(center);
            draw(tex, width, lineLen, (float) 0, (float) 0, position.x, position.y, angle + angleStepH, col,addative);
        }
        SolMath.free(position);
    }

    public void drawLine(TextureRegion tex, float x, float y, float angle, float len, Color col, float width,boolean additive) {
        draw(tex, len, width, 0, width / 2, x, y, angle, col,additive);
    }

    public void draw(ParticleEmitter emitter, TextureAtlas.AtlasRegion tex, boolean additive) {
        setAdditive(additive);
        emitter.draw(getSpriteBatch());
    }

    public void drawLine(TextureRegion tex, Vector2 startPoint, Vector2 endPoint, Color color, float width, boolean precise,boolean additive) {
        Vector2 endPointCopy = SolMath.getVec(endPoint);
        endPointCopy.sub(startPoint);
        drawLine(tex, startPoint.x, startPoint.y, SolMath.angle(endPointCopy), endPointCopy.len(), color, width,additive);
        SolMath.free(endPointCopy);
    }

    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    private void setAdditive(boolean additive) {
        int dstFunc = additive ? GL20.GL_ONE : GL20.GL_ONE_MINUS_SRC_ALPHA;
        spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, dstFunc);
    }

    @Override
    public void resize() {
        screenViewport.update(displayDimensions.getWidth(), displayDimensions.getHeight(), true);
    }
}
