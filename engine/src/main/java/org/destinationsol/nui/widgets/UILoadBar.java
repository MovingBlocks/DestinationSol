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
package org.destinationsol.nui.widgets;

import org.terasology.engine.Time;
import org.terasology.math.TeraMath;
import com.badlogic.gdx.math.Rectangle;
import org.terasology.math.geom.Vector2;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.destinationsol.nui.Canvas;
import org.destinationsol.nui.CoreWidget;
import org.destinationsol.nui.LayoutConfig;
import org.destinationsol.nui.ScaleMode;
import org.destinationsol.nui.databinding.Binding;
import org.destinationsol.nui.databinding.DefaultBinding;

/**
 * An animated loading bar
 */
public class UILoadBar extends CoreWidget {

    @LayoutConfig
    private TextureRegion fillTexture;

    @LayoutConfig
    private boolean animate = true;

    @LayoutConfig
    private Binding<Float> value = new DefaultBinding<>(0f);
    private Time time = CoreRegistry.get(Time.class);

    @Override
    public void onDraw(Canvas canvas) {
        if (fillTexture != null) {
            int size = TeraMath.floorToInt(canvas.size().x * getValue());
            int barWidth = fillTexture.getWidth();
            int offset = 0;
            if (time != null && animate) {
                offset = (int) ((time.getRealTimeInMs() / 10) % barWidth);
            }
            int drawnWidth = 0;
            // Draw Offset
            if (offset != 0) {
                int drawWidth = Math.min(size, offset);
                canvas.drawTextureRaw(fillTexture, Rectangle.createFromMinAndSize(0, 0, drawWidth, canvas.size().y),
                        ScaleMode.STRETCH, barWidth - offset, 0, drawWidth, canvas.size().y);
                drawnWidth += drawWidth;
            }
            // Draw Remainder
            while (drawnWidth < size) {
                int drawWidth = Math.min(size - drawnWidth, barWidth);
                canvas.drawTextureRaw(fillTexture, Rectangle.createFromMinAndSize(drawnWidth, 0, drawWidth, canvas.size().y),
                        ScaleMode.STRETCH, 0, 0, drawWidth, canvas.size().y);
                drawnWidth += drawWidth;
            }
        }
    }

    @Override
    public Vector2 getPreferredContentSize(Canvas canvas, Vector2 sizeHint) {
        return Vector2.zero();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    /**
     * @return The texture used in the bar.
     */
    public TextureRegion getFillTexture() {
        return fillTexture;
    }

    /**
     * @param fillTexture The new texture to use.
     */
    public void setFillTexture(TextureRegion fillTexture) {
        this.fillTexture = fillTexture;
    }

    /**
     * @return A Boolean indicating whether. the bar is animated.
     */
    public boolean isAnimate() {
        return animate;
    }

    /**
     * @param animate A Boolean indicating if the bar should be animated.
     */
    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public void bindValue(Binding<Float> binding) {
        value = binding;
    }

    /**
     * @return A Float between 0 and 1 indicating the percentage of the bar.
     */
    public float getValue() {
        return value.get();
    }

    /**
     * @param val A float from 0 to 1 indicating the percentage loaded.
     */
    public void setValue(float val) {
        value.set(val);
    }
}
