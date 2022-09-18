/*
 * Copyright 2021 The Terasology Foundation
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

package org.destinationsol.ui.nui.widgets;

import org.joml.Vector2i;
import org.terasology.joml.geom.Rectanglei;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.CoreWidget;
import org.terasology.nui.HorizontalAlign;
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.TextLineBuilder;
import org.terasology.nui.UITextureRegion;
import org.terasology.nui.asset.font.Font;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;

import java.util.List;

/**
 * This widget represents an icon proceeded or succeeded by a label, treated as a composite unit.
 * The label consists typically of annotative text that describes the icon.
 * Both the text and the icon are optional, however it is advised to use at least one of them or both.
 */
public class UILabelledIcon extends CoreWidget {
    @LayoutConfig
    private Binding<String> text = new DefaultBinding<>();
    @LayoutConfig
    private Binding<UITextureRegion> icon = new DefaultBinding<>();
    @LayoutConfig
    private Binding<Color> iconTint = new DefaultBinding<>((Color) Color.white);
    @LayoutConfig
    private int spacing = 8;
    @LayoutConfig
    private HorizontalAlign iconAlign = HorizontalAlign.LEFT;

    public UILabelledIcon() {
    }

    public UILabelledIcon(String id, String text, UITextureRegion icon) {
        super(id);
        this.text.set(text);
        this.icon.set(icon);
    }

    public UILabelledIcon(String id, String text) {
        super(id);
        this.text.set(text);
    }

    public UILabelledIcon(String id, UITextureRegion icon) {
        super(id);
        this.icon.set(icon);
    }

    public UILabelledIcon(String text) {
        this.text.set(text);
    }

    public UILabelledIcon(UITextureRegion icon) {
        this.icon.set(icon);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Vector2i iconSize = new Vector2i();
        Vector2i textSize = new Vector2i();
        if (icon.get() != null) {
            iconSize.x = icon.get().getWidth();
            iconSize.y = icon.get().getHeight();
        }
        if (text.get() != null) {
            Font font = canvas.getCurrentStyle().getFont();
            List<String> lines = TextLineBuilder.getLines(font, text.get(), canvas.size().x - iconSize.x - spacing);
            textSize = font.getSize(lines);
        }

        switch (iconAlign) {
            case LEFT:
                if (icon.get() != null) {
                    canvas.drawTexture(icon.get(), new Rectanglei(0, 0, iconSize.x, iconSize.y), iconTint.get());
                }
                if (text.get() != null) {
                    canvas.drawText(text.get(), new Rectanglei(iconSize.x + spacing, 0, canvas.size().x, canvas.size().y));
                }
                break;
            case RIGHT:
                // Fallthrough
            case CENTER:
                int iconOffsetX = textSize.x + iconAlign.getOffset(iconSize.x, canvas.size().x - textSize.x) + spacing;
                if (text.get() != null) {
                    canvas.drawText(text.get(), new Rectanglei(0, 0, canvas.size().x - iconSize.x, canvas.size().y));
                }
                if (icon.get() != null) {
                    canvas.drawTexture(icon.get(), new Rectanglei(iconOffsetX, 0, iconOffsetX + iconSize.x, canvas.size().y), iconTint.get());
                }
                break;
        }
    }

    /**
     * Returns the current label text.
     * @return the current label text.
     */
    public String getText() {
        return text.get();
    }

    /**
     * Sets the new label text.
     * @param text the new label text.
     */
    public void setText(String text) {
        this.text.set(text);
    }

    /**
     * Binds the label text to the specified binding.
     * @param text the binding to use.
     */
    public void bindText(Binding<String> text) {
        this.text = text;
    }

    /**
     * Returns the current icon in use.
     * @return the current icon.
     */
    public UITextureRegion getIcon() {
        return icon.get();
    }

    /**
     * Sets the new icon to be used.
     * @param icon the new icon to use.
     */
    public void setIcon(UITextureRegion icon) {
        this.icon.set(icon);
    }

    /**
     * Binds the icon displayed to the specified binding.
     * @param icon the binding to use.
     */
    public void bindIcon(Binding<UITextureRegion> icon) {
        this.icon = icon;
    }

    /**
     * Returns the current tint of the icon.
     * @return the current icon tint.
     */
    public Color getIconTint() {
        return iconTint.get();
    }

    /**
     * Sets the new tint for the icon.
     * @param tint the new icon tint.
     */
    public void setIconTint(Color tint) {
        iconTint.set(tint);
    }

    /**
     * Binds the icon tint to the specified binding.
     * @param tint the binding to use.
     */
    public void bindIconTint(Binding<Color> tint) {
        this.iconTint = tint;
    }

    /**
     * Returns the current spacing between the text and the icon.
     * @return the current text/icon spacing.
     */
    public int getSpacing() {
        return spacing;
    }

    /**
     * Sets the new spacing between the text and the icon.
     * @param spacing the new text/icon spacing.
     */
    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    /**
     * Returns the current icon alignment (left, right or center)
     * @return the current icon alignment.
     */
    public HorizontalAlign getIconAlign() {
        return iconAlign;
    }

    /**
     * Sets the new icon alignment (left, right or center).
     * @param iconAlign the new icon alignment.
     */
    public void setIconAlign(HorizontalAlign iconAlign) {
        this.iconAlign = iconAlign;
    }

    /**
     * Returns the preferred content size of this widget.
     *
     * @param canvas   A {@link Canvas} on which this widget is drawn.
     * @param sizeHint A {@link Vector2i} representing how much available space is for this widget.
     * @return A {@link Vector2i} which represents the preferred size of this widget.
     */
    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        Vector2i size = new Vector2i();
        if (text.get() != null) {
            Font font = canvas.getCurrentStyle().getFont();
            List<String> lines = TextLineBuilder.getLines(font, getText(), sizeHint.x);
            size.add(font.getSize(lines));
        }

        if (icon.get() != null) {
            size.add(icon.get().size());
        }

        size.x += spacing;
        return size;
    }
}
