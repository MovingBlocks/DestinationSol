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

import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.UITextureRegion;
import org.terasology.nui.UIWidget;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;
import org.terasology.nui.widgets.UISpace;

/**
 * An informational box that appears in the screen to alert the user to a particular circumstance that has occurred.
 * The background, background tint, box content and trigger conditions are all configurable.
 */
public class UIWarnDrawer extends EmptyIfInvisibleContainer {
    private static final float FADE_TIME = 1f;
    @LayoutConfig
    private UITextureRegion background;
    @LayoutConfig
    private Color tint;
    private float alpha;
    private Binding<Boolean> shouldWarn = new DefaultBinding<>(false);

    public UIWarnDrawer(String id, UITextureRegion background, Color tint) {
        super(id, new UISpace());
        this.background = background;
        this.tint = tint;
        this.alpha = 0.0f;
    }

    public UIWarnDrawer(String id, UITextureRegion background, Color tint, UIWidget content) {
        super(id, content);
        this.background = background;
        this.tint = tint;
        this.alpha = 0.0f;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.setAlpha(alpha);
        canvas.drawTexture(background, tint);
        super.onDraw(canvas);
    }

    @Override
    public void update(float delta) {
        if (isWarning()) {
            alpha = 1;
        } else {
            alpha = SolMath.approach(alpha, 0, Const.REAL_TIME_STEP / FADE_TIME);
        }

        super.update(delta);
    }

    /**
     * Returns the current background tint.
     * @return the current background tint.
     */
    public Color getTint() {
        return tint;
    }

    /**
     * Sets the new background tint.
     * @param tint the new background tint.
     */
    public void setTint(Color tint) {
        this.tint = tint;
    }

    /**
     * Returns the current background texture.
     * @return the current background texture.
     */
    public UITextureRegion getBackground() {
        return background;
    }

    /**
     * Sets the new background texture.
     * @param background the new background texture.
     */
    public void setBackground(UITextureRegion background) {
        this.background = background;
    }

    /**
     * Returns the current transparency of the widget. This is additive relative to the canvas.
     * @return the current widget transparency.
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Sets the new widget transparency, relative to the canvas.
     * The assigned value may be overridden if the widget is currently warning.
     * @param alpha the new widget transparency.
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    /**
     * Returns if the widget is currently warning (full opacity).
     * @return true, if the widget is currently warning, otherwise false.
     */
    public boolean isWarning() {
        return shouldWarn.get();
    }

    /**
     * Sets whether the widget should currently be warning.
     * @param shouldWarn true, if the widget should currently be warning, otherwise false.
     */
    public void setWarn(boolean shouldWarn) {
        this.shouldWarn.set(shouldWarn);
    }

    /**
     * Assigns a binding to the widget's warning value to determine when it should warn.
     * @param shouldWarn the new warning binding.
     */
    public void bindWarn(Binding<Boolean> shouldWarn) {
        this.shouldWarn = shouldWarn;
    }

    /**
     * Returns if the widget is visible.
     * NOTE: This overridden version also returns true if the widget alpha is zero or less.
     */
    @Override
    public boolean isVisible() {
        if (!super.isVisible()) {
            return false;
        }

        return alpha > 0.0f;
    }
}
