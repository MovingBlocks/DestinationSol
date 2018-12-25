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

import org.terasology.math.TeraMath;
import com.badlogic.gdx.math.Rectangle;
import org.terasology.math.geom.Vector2;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.destinationsol.nui.Canvas;
import org.destinationsol.nui.CoreWidget;
import org.destinationsol.nui.LayoutConfig;
import org.destinationsol.nui.ScaleMode;
import org.destinationsol.nui.databinding.Binding;
import org.destinationsol.nui.databinding.DefaultBinding;
import org.destinationsol.nui.skin.UIStyle;

/**
 * A value bar that uses Icons
 */
public class UIIconBar extends CoreWidget {

    private static final String ICON_PART = "icon";

    @LayoutConfig
    private HalfIconMode halfIconMode = HalfIconMode.SPLIT;

    @LayoutConfig
    private int maxIcons = 10;

    @LayoutConfig
    private int spacing = 1;

    @LayoutConfig
    private TextureRegion icon;

    @LayoutConfig
    private Binding<Float> value = new DefaultBinding<>(0f);

    @LayoutConfig
    private Binding<Float> maxValue = new DefaultBinding<>(10f);

    @Override
    public void onDraw(Canvas canvas) {
        canvas.setPart(ICON_PART);
        if (icon != null && getMaxValue() > 0) {
            Vector2 iconSize = getIconSize(canvas);
            float ratio = maxIcons * getValue() / getMaxValue();
            int fullIcons = TeraMath.floorToInt(ratio);
            boolean halfIcon = false;
            if (ratio - fullIcons >= 0.5f) {
                fullIcons++;
            } else if (ratio - fullIcons > 0) {
                halfIcon = true;
            }
            Vector2 offset = new Vector2();
            for (int i = 0; i < maxIcons; ++i) {
                Rectangle iconRegion = Rectangle.createFromMinAndSize(offset, iconSize);
                canvas.drawBackground(iconRegion);
                if (ratio - i >= 0.5f) {
                    canvas.drawTexture(icon, iconRegion);
                } else if (ratio - i > 0f) {
                    switch (halfIconMode) {
                        case SHRINK:
                            Vector2 halfSize = new Vector2(iconSize);
                            halfSize.x /= 2;
                            halfSize.y /= 2;
                            canvas.drawTexture(icon,
                                    Rectangle.createFromMinAndSize(new Vector2(offset.x + halfSize.x / 2, offset.y + halfSize.y / 2), halfSize));
                            break;
                        case SPLIT:
                            canvas.drawTextureRaw(icon,
                                    Rectangle.createFromMinAndSize(offset, new Vector2(iconSize.x / 2, iconSize.y)),
                                    ScaleMode.STRETCH, 0f, 0f, (float) (iconSize.x / 2) / iconSize.x, 1.0f);
                            break;
                        default:
                            canvas.drawTexture(icon, iconRegion);
                            break;
                    }
                }
                offset.x += iconSize.x + spacing;
                if (offset.x + iconSize.x > canvas.size().x) {
                    offset.x = 0;
                    offset.y += iconSize.y + spacing;
                }
            }
        }
    }

    @Override
    public Vector2 getPreferredContentSize(Canvas canvas, Vector2 sizeHint) {
        canvas.setPart(ICON_PART);
        if (icon != null) {
            Vector2 iconSize = getIconSize(canvas);
            int maxHorizontalIcons = sizeHint.x / iconSize.x;
            int rows = ((maxIcons - 1) / maxHorizontalIcons) + 1;
            int columns = Math.min(maxIcons, maxHorizontalIcons);
            return new Vector2(columns * iconSize.x + (columns - 1) * spacing, rows * iconSize.y + (rows - 1) * spacing);
        } else {
            return Vector2.zero();
        }

    }

    private Vector2 getIconSize(Canvas canvas) {
        UIStyle iconStyle = canvas.getCurrentStyle();
        int width = iconStyle.getFixedWidth();
        int height = iconStyle.getFixedHeight();
        if (width == 0) {
            width = iconStyle.getMinWidth();
        }
        if (height == 0) {
            height = iconStyle.getMinHeight();
        }
        if (width == 0) {
            width = icon.getWidth();
        }
        if (height == 0) {
            height = icon.getHeight();
        }
        return new Vector2(width, height);
    }

    /**
     * @return The icon used.
     */
    public TextureRegion getIcon() {
        return icon;
    }

    /**
     * @param icon The icon to use in the bar.
     */
    public void setIcon(TextureRegion icon) {
        this.icon = icon;
    }

    public void bindValue(Binding<Float> binding) {
        value = binding;
    }

    /**
     * @return The current value.
     */
    public float getValue() {
        return value.get();
    }

    /**
     * @param val The value to set it to.
     */
    public void setValue(float val) {
        value.set(val);
    }

    public void bindMaxValue(Binding<Float> binding) {
        maxValue = binding;
    }

    /**
     * @return The maximum value the bar can be set to.
     */
    public float getMaxValue() {
        return maxValue.get();
    }

    /**
     * @param val The new max value the bar can be set to.
     */
    public void setMaxValue(float val) {
        maxValue.set(val);
    }

    /**
     * @return The current HalfIconMode used.
     */
    public HalfIconMode getHalfIconMode() {
        return halfIconMode;
    }

    /**
     * @param halfIconMode The new mode to use.
     */
    public void setHalfIconMode(HalfIconMode halfIconMode) {
        this.halfIconMode = halfIconMode;
    }

    public enum HalfIconMode {
        NONE,
        SPLIT,
        SHRINK,
    }


}
