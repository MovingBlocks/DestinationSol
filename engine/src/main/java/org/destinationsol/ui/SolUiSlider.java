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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.assets.Assets;
import org.destinationsol.common.SolColor;

public class SolUiSlider {
    private static final TextureAtlas.AtlasRegion sliderTexture = Assets.getAtlasRegion("engine:ui/slider");
    private static final TextureAtlas.AtlasRegion sliderMarkerTexture = Assets.getAtlasRegion("engine:ui/sliderMarker");

    private final Rectangle rectangle;
    private float value;
    public SolUiSlider(Rectangle sliderRectangle, float startingValue) {
        rectangle = sliderRectangle;
        value = startingValue;
    }

    public void draw(UiDrawer uiDrawer) {
        uiDrawer.draw(rectangle, SolColor.UI_INACTIVE);
        uiDrawer.draw(sliderTexture, rectangle.width, rectangle.height, 0, 0, rectangle.x, rectangle.y, 0, Color.WHITE);

        uiDrawer.setTextMode(true);
        uiDrawer.drawString("Red: " + value, rectangle.x + rectangle.width/2, rectangle.y + rectangle.height/2, 1, true, Color.WHITE);
        uiDrawer.setTextMode(false);
        uiDrawer.draw(sliderMarkerTexture, rectangle.width/80, rectangle.height, 0, 0, rectangle.x + value * rectangle.width, rectangle.y, 0, Color.WHITE );
    }

    public void click(Vector2 clickPosition) {
        if(clickPosition.x > rectangle.x && clickPosition.x < rectangle.x + rectangle.width &&
            clickPosition.y > rectangle.y && clickPosition.y < rectangle.y + rectangle.height) {
            float relativePos = clickPosition.x - rectangle.x;
            value = relativePos/rectangle.width;
        }
    }

    public void setValue(float val) {
        if(val < 0) {
            value = 0;
            return;
        } else if (val > 1.0f) {
            value = 1.0f;
            return;
        }
        value = val;
    }

    public float getValue() {
        return value;
    }
}
