/*
 * Copyright 2019 MovingBlocks
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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;
import org.destinationsol.common.SolMath;

public class SolUiSlider {
    private static final float HANDLE_SCALE = 0.0125f;

    private static final TextureAtlas.AtlasRegion sliderTexture = Assets.getAtlasRegion("engine:ui/slider");
    private static final TextureAtlas.AtlasRegion sliderMarkerTexture = Assets.getAtlasRegion("engine:ui/sliderMarker");

    private final Rectangle rectangle;
    private float value;
    private String text;
    private final int trimAt;

    public SolUiSlider(Rectangle sliderRectangle, String text, float startingValue, int trimAt) {
        rectangle = sliderRectangle;
        value = SolMath.clamp(value);
        this.text = text;
        this.trimAt = trimAt;
    }

    public void draw(UiDrawer uiDrawer) {
        uiDrawer.draw(rectangle, SolColor.UI_INACTIVE);
        uiDrawer.draw(sliderTexture, rectangle.width, rectangle.height, 0, 0, rectangle.x, rectangle.y, 0, SolColor.WHITE);

        uiDrawer.setTextMode(true);
        String trimmedValue = Float.toString(value);
        int length = trimmedValue.substring(trimmedValue.indexOf('.')).length();
        if(length > trimAt) {
            String leftSubstring = trimmedValue.substring(0, trimmedValue.indexOf('.'));
            String rightSubstring = trimmedValue.substring(trimmedValue.indexOf('.'), leftSubstring.length() + 1 + trimAt);
            trimmedValue = leftSubstring + rightSubstring;
        }

        uiDrawer.drawString(text + trimmedValue, rectangle.x + rectangle.width * 0.5f, rectangle.y + rectangle.height * 0.5f, 1, true, SolColor.WHITE);
        uiDrawer.setTextMode(false);
        uiDrawer.draw(sliderMarkerTexture, rectangle.width * HANDLE_SCALE, rectangle.height, rectangle.width * HANDLE_SCALE * 0.5f, 0, rectangle.x + value * rectangle.width, rectangle.y, 0, SolColor.WHITE);
    }

    public boolean click(Vector2 clickPosition) {
        if(clickPosition.x > rectangle.x && clickPosition.x < rectangle.x + rectangle.width &&
            clickPosition.y > rectangle.y && clickPosition.y < rectangle.y + rectangle.height) {
            float relativePos = clickPosition.x - rectangle.x;
            setValue(relativePos / rectangle.width);
            return true;
        }
        return false;
    }

    public void setValue(float val) {
        value = SolMath.clamp(val);
    }

    public float getValue() {
        return value;
    }
}
