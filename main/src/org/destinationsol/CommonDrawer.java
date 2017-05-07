/*
 * Copyright 2015 MovingBlocks
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

package org.destinationsol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.common.SolMath;
import org.destinationsol.files.FileManager;
import org.destinationsol.TextAlignment;

import javax.swing.text.TabExpander;
import javax.xml.soap.Text;

public class CommonDrawer {
    public final float w;
    public final float h;
    public final float r;

    private final SpriteBatch mySpriteBatch;
    private final BitmapFont myFont;
    private final float myOrigFontHeight;
    private final TextureChecker myTextureChecker;
    private final GlyphLayout layout;

    public CommonDrawer() {
        myTextureChecker = new TextureChecker();
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        r = w / h;
        mySpriteBatch = new SpriteBatch();

        final FileHandle fontFile = FileManager.getInstance().getFontsDirectory().child("main.fnt");
        myFont = new BitmapFont(fontFile, true);
        myFont.setUseIntegerPositions(false);

        myOrigFontHeight = myFont.getXHeight();

        layout = new GlyphLayout();
    }

    public void setMtx(Matrix4 mtx) {
        mySpriteBatch.setProjectionMatrix(mtx);
    }

    public void begin() {
        mySpriteBatch.begin();
    }

    public void end() {
        myTextureChecker.onEnd();
        mySpriteBatch.end();
    }

    public void drawString(String s, float x, float y, float fontSize, boolean centered, Color col) {
        drawString(s, x, y, fontSize, TextAlignment.CENTER, centered, col);
    }

    public void drawString(String s, float x, float y, float fontSize, TextAlignment align, boolean verticalCentering, Color col) {
        if (s == null) return;

        myTextureChecker.onString(myFont.getRegion().getTexture());
        myFont.setColor(col);
        myFont.getData().setScale(fontSize / myOrigFontHeight);
        // http://www.badlogicgames.com/wordpress/?p=3658
        layout.reset();
        layout.setText(myFont, s);

        switch(align){
            case LEFT:
                break;
            case CENTER:
                x -= layout.width / 2;
                break;
            case RIGHT:
                x -= layout.width;
                break;
        }

        if(verticalCentering) {
            y -= layout.height / 2;
        }

        myFont.draw(mySpriteBatch, layout, x, y);
    }

    public void draw(TextureRegion tr, float width, float height, float origX, float origY, float x, float y,
                     float rot, Color tint) {
        setTint(tint);
        if (tr instanceof TextureAtlas.AtlasRegion) {
            myTextureChecker.onReg((TextureAtlas.AtlasRegion) tr);
        } else {
            throw new AssertionError("Unexpected texture class");
        }
        mySpriteBatch.draw(tr, x - origX, y - origY, origX, origY, width, height, 1, 1, rot);
    }

    private void setTint(Color tint) {
        mySpriteBatch.setColor(tint);
    }

    public void draw(TextureRegion tex, Rectangle rect, Color tint) {
        draw(tex, rect.width, rect.height, (float) 0, (float) 0, rect.x, rect.y, (float) 0, tint);
    }

    public void drawCircle(TextureRegion tex, Vector2 center, float radius, Color col, float width, float vh) {
        float relRad = radius / vh;
        int pointCount = (int) (160 * relRad);
        Vector2 pos = SolMath.getVec();
        if (pointCount < 8) pointCount = 8;
        float lineLen = radius * SolMath.PI * 2 / pointCount;
        float angleStep = 360f / pointCount;
        float angleStepH = angleStep / 2;
        for (int i = 0; i < pointCount; i++) {
            float angle = angleStep * i;
            SolMath.fromAl(pos, angle, radius);
            pos.add(center);
            draw(tex, width, lineLen, (float) 0, (float) 0, pos.x, pos.y, angle + angleStepH, col);
        }
        SolMath.free(pos);
    }

    public void drawLine(TextureRegion tex, float x, float y, float angle, float len, Color col, float width) {
        draw(tex, len, width, 0, width / 2, x, y, angle, col);
    }

    public void drawLine(TextureRegion tex, Vector2 p1, Vector2 p2, Color col, float width, boolean precise) {
        Vector2 v = SolMath.getVec(p2);
        v.sub(p1);
        drawLine(tex, p1.x, p1.y, SolMath.angle(v, precise), v.len(), col, width);
        SolMath.free(v);
    }

    public void dispose() {
        mySpriteBatch.dispose();
        myFont.dispose();
    }

    public SpriteBatch getBatch(Texture texture, TextureAtlas.AtlasRegion tex) {
        myTextureChecker.onSprite(texture, tex);
        return mySpriteBatch;
    }

    public void setAdditive(boolean additive) {
        int dstFunc = additive ? GL20.GL_ONE : GL20.GL_ONE_MINUS_SRC_ALPHA;
        mySpriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, dstFunc);
    }
}
