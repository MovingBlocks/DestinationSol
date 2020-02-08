/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.assets.fonts;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import org.terasology.math.geom.Vector2i;
import org.terasology.nui.backends.libgdx.LibGDXFont;

import java.util.List;

/**
 * This class wraps a {@link Font} asset in order to adapt it to NUI's LibGDX back-end.
 */
public class UIFont extends LibGDXFont {
    private float fontScale;
    private float previousFontScaleX;
    private float previousFontScaleY;
    private static final float FONT_SCALE = 8.0f;

    public UIFont(Font font) {
        super(font.getBitmapFont());

        fontScale = FONT_SCALE / super.getGdxFont().getData().xHeight;
    }

    private void scale() {
        scale(fontScale, fontScale);
    }

    private void scale(float scaleX, float scaleY) {
        previousFontScaleX = super.getGdxFont().getScaleX();
        previousFontScaleY = super.getGdxFont().getScaleY();
        super.getGdxFont().getData().setScale(scaleX, scaleY);
    }

    private void reset() {
        super.getGdxFont().getData().setScale(previousFontScaleX, previousFontScaleY);
    }

    @Override
    public Vector2i getSize(List<String> lines) {
        scale();
        Vector2i result = super.getSize(lines);
        reset();
        return result;
    }

    @Override
    public int getWidth(Character c) {
        scale();
        int result = super.getWidth(c);
        reset();
        return result;
    }

    @Override
    public int getLineHeight() {
        scale();
        int result = super.getLineHeight();
        reset();
        return result;
    }

    @Override
    public int getHeight(String text) {
        scale();
        int result = super.getHeight(text);
        reset();
        return result;
    }

    @Override
    public int getBaseHeight() {
        scale();
        int result = super.getBaseHeight();
        reset();
        return result;
    }

    @Override
    public int getWidth(String text) {
        scale();
        int result = super.getWidth(text);
        reset();
        return result;
    }

    @Override
    public BitmapFont getGdxFont() {
        super.getGdxFont().getData().setScale(fontScale, -fontScale);
        return super.getGdxFont();
    }

    @Override
    public GlyphLayout getGlyphLayout() {
        super.getGdxFont().getData().setScale(fontScale, -fontScale);
        return super.getGlyphLayout();
    }
}
