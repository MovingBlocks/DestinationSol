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


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.destinationsol.nui.Canvas;
import org.destinationsol.nui.CoreWidget;
import org.destinationsol.nui.LayoutConfig;
import org.destinationsol.nui.ScaleMode;
import org.destinationsol.nui.databinding.Binding;
import org.destinationsol.nui.databinding.DefaultBinding;

/**
 *  A widget to display an image
 */
public class UIImage extends CoreWidget {
    @LayoutConfig
    private Binding<TextureRegion> image = new DefaultBinding<>();

    @LayoutConfig
    private Binding<Color> tint = new DefaultBinding<>(Color.WHITE);

    @LayoutConfig
    private boolean ignoreAspectRatio;

    public UIImage() {
    }

    public UIImage(String id) {
        super(id);
    }

    public UIImage(TextureRegion image) {
        this.image.set(image);
    }

    public UIImage(String id, TextureRegion image) {
        super(id);
        this.image.set(image);
    }

    public UIImage(String id, TextureRegion image, boolean ignoreAspectRatio) {
        super(id);
        this.image.set(image);
        this.ignoreAspectRatio = ignoreAspectRatio;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (image.get() != null) {
            if (ignoreAspectRatio) {
                ScaleMode scaleMode = canvas.getCurrentStyle().getTextureScaleMode();
                canvas.getCurrentStyle().setTextureScaleMode(ScaleMode.STRETCH);
                canvas.drawTexture(image.get(), tint.get());
                canvas.getCurrentStyle().setTextureScaleMode(scaleMode);
            } else {
                canvas.drawTexture(image.get(), tint.get());
            }
        }
    }

    @Override
    public Vector2 getPreferredContentSize(Canvas canvas, Vector2 sizeHint) {
        if (image.get() != null) {
            return image.get().size();
        }
        return Vector2.zero();
    }

    /**
     * @return The image being displayed
     */
    public TextureRegion getImage() {
        return image.get();
    }

    /**
     * @param image The new image to display.
     */
    public void setImage(TextureRegion image) {
        this.image.set(image);
    }

    public void bindTexture(Binding<TextureRegion> binding) {
        this.image = binding;
    }

    /**
     * @return The Color of the tint.
     */
    public Color getTint() {
        return tint.get();
    }

    /**
     * @param color The new tint to apply.
     */
    public void setTint(Color color) {
        this.tint.set(color);
    }

    public void bindTint(Binding<Color> binding) {
        this.tint = binding;
    }

}
