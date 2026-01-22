/*
 * Copyright 2026 The Terasology Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
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
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.ScaleMode;
import org.terasology.nui.UITextureRegion;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;

import java.util.List;

/**
 * A widget to display an animated image based on a spritesheet.
 */
public class UIAnimatedImage extends CoreWidget {
    @LayoutConfig
    private Binding<UITextureRegion> spritesheet = new DefaultBinding<>();
    @LayoutConfig
    private Binding<Float> frameDuration = new DefaultBinding<>();
    @LayoutConfig
    private Binding<List<Rectanglei>> frames = new DefaultBinding<>();

    @LayoutConfig
    private Binding<Color> tint = new DefaultBinding<>(Color.WHITE);

    @LayoutConfig
    private boolean ignoreAspectRatio;

    private float frameTimer;
    private int frameNo;

    public UIAnimatedImage() {
    }

    public UIAnimatedImage(String id) {
        super(id);
    }

    public UIAnimatedImage(UITextureRegion spritesheet, float frameDuration, List<Rectanglei> frames) {
        this.spritesheet.set(spritesheet);
        this.frameDuration.set(frameDuration);
        this.frames.set(frames);
        this.frameTimer = 0;
        this.frameNo = 0;
    }

    public UIAnimatedImage(String id, UITextureRegion spritesheet, float frameDuration, List<Rectanglei> frames) {
        super(id);
        this.spritesheet.set(spritesheet);
        this.frameDuration.set(frameDuration);
        this.frames.set(frames);
        this.frameTimer = 0;
        this.frameNo = 0;
    }

    public UIAnimatedImage(String id, UITextureRegion spritesheet, float frameDuration, List<Rectanglei> frames, boolean ignoreAspectRatio) {
        super(id);
        this.spritesheet.set(spritesheet);
        this.frameDuration.set(frameDuration);
        this.frames.set(frames);
        this.frameTimer = 0;
        this.frameNo = 0;
        this.ignoreAspectRatio = ignoreAspectRatio;
    }

    @Override
    public void update(float delta) {
        frameTimer += delta;
        if (frameTimer >= frameDuration.get()) {
            frameTimer = 0;
            frameNo = (frameNo + 1) % frames.get().size();
        }
        super.update(delta);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (spritesheet.get() != null || frames.get().isEmpty()) {
            Rectanglei currentFrame = frames.get().get(frameNo);
            if (ignoreAspectRatio) {
                ScaleMode scaleMode = canvas.getCurrentStyle().getTextureScaleMode();

                if (spritesheet.get().getWidth() > (spritesheet.get().getHeight() * 2)) {
                    canvas.getCurrentStyle().setTextureScaleMode(ScaleMode.STRETCH);
                } else {
                    canvas.getCurrentStyle().setTextureScaleMode(ScaleMode.SCALE_FILL);
                }
                canvas.drawTextureRaw(spritesheet.get(), canvas.getRegion(), tint.get(),
                        canvas.getCurrentStyle().getTextureScaleMode(),
                        (float) currentFrame.minX() / spritesheet.get().getWidth(),
                        (float) currentFrame.minY() / spritesheet.get().getHeight(),
                        (float) currentFrame.getSizeX() / spritesheet.get().getWidth(),
                        (float) currentFrame.getSizeY() / spritesheet.get().getHeight()
                );
                canvas.getCurrentStyle().setTextureScaleMode(scaleMode);
            } else {
                canvas.drawTextureRaw(spritesheet.get(), canvas.getRegion(), tint.get(),
                        canvas.getCurrentStyle().getTextureScaleMode(),
                        (float) currentFrame.minX() / spritesheet.get().getWidth(),
                        (float) currentFrame.minY() / spritesheet.get().getHeight(),
                        (float) currentFrame.getSizeX() / spritesheet.get().getWidth(),
                        (float) currentFrame.getSizeY() / spritesheet.get().getHeight()
                );
            }
        }
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        if (spritesheet.get() != null && !frames.get().isEmpty()) {
            if (frameNo >= frames.get().size()) {
                frameNo = 0;
            }
            return frames.get().get(frameNo).getSize(new Vector2i());
        }
        return new Vector2i();
    }

    /**
     * @return The spritesheet image being used.
     */
    public UITextureRegion getSpritesheet() {
        return spritesheet.get();
    }

    /**
     * @param spritesheet The new spritesheet image to use.
     */
    public void setSpritesheet(UITextureRegion spritesheet) {
        this.spritesheet.set(spritesheet);
    }

    public void bindSpritesheet(Binding<UITextureRegion> binding) {
        this.spritesheet = binding;
    }

    public void setFrameDuration(float frameDuration) {
        this.frameDuration.set(frameDuration);
    }

    public void bindFrameDuration(Binding<Float> frameDuration) {
        this.frameDuration = frameDuration;
    }

    public void setFrames(List<Rectanglei> frames) {
        this.frames.set(frames);
    }

    public void bindFrame(Binding<List<Rectanglei>> frames) {
        this.frames = frames;
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
