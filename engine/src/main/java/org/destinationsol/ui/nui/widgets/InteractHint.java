/*
 * Copyright 2023 The Terasology Foundation
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
import org.terasology.input.Input;
import org.terasology.input.InputType;
import org.terasology.input.Keyboard;
import org.terasology.input.MouseInput;
import org.terasology.nui.Canvas;
import org.terasology.nui.CoreWidget;
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;

/**
 * This widget displays an icon representing the desired input interaction.
 * The icons used are determined by the UI style assigned to the widget.
 * The widget's part is set to the input type and its mode set to the exact input name (lowercase).
 */
public class InteractHint extends CoreWidget {
    public static String MOBILE_TAP_MODE = "mobile_tap";
    private Binding<Input> input = new DefaultBinding<>(Keyboard.Key.NONE);
    /**
     * Determines if mouse left-click actions should use the {@link #MOBILE_TAP_MODE} mode instead.
     */
    @LayoutConfig
    private boolean useMobileIcons;

    public InteractHint() {
    }

    public InteractHint(String id, Input input) {
        setId(id);
        this.input.set(input);
    }

    /**
     * Returns the input being hinted at.
     * @return the hinted input
     */
    public Input getInput() {
        return input.get();
    }

    /**
     * Sets the input to be hinted.
     * @param input the input to be hinted
     */
    public void setInput(Input input) {
        this.input.set(input);
    }

    /**
     * Binds the input to be hinted to the specified binding.
     * @param input the input binding
     */
    public void bindInput(Binding<Input> input) {
        this.input = input;
    }

    /**
     * States if the hint uses a mobile icon for the mouse left-click input.
     * @return true, if using a mobile icon for left-click, otherwise false
     */
    public boolean isUsingMobileIcons() {
        return useMobileIcons;
    }

    /**
     * Specifies if a mobile icon should be used for the mouse left-click hint.
     * @param useMobileIcons determines if the mobile icon should be used
     */
    public void useMobileIcons(boolean useMobileIcons) {
        this.useMobileIcons = useMobileIcons;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.setPart(input.get().getType().toString().toLowerCase());
        canvas.drawBackground();
        if (input.get().getType() == InputType.KEY) {
            canvas.drawText(input.get().getDisplayName(), canvas.getCurrentStyle().getBackground().getPixelRegion());
        }

        if (input.get().getType() == InputType.CONTROLLER_BUTTON || input.get().getType() == InputType.CONTROLLER_AXIS) {
            canvas.drawText(String.valueOf(input.get().getId()), canvas.getCurrentStyle().getBackground().getPixelRegion());
        }
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        canvas.setPart(input.get().getType().toString().toLowerCase());
        return canvas.getCurrentStyle().getBackground().size();
    }

    @Override
    public String getMode() {
        if (!isEnabled()) {
            return DISABLED_MODE;
        }
        if (input.get() == null) {
            return DEFAULT_MODE;
        }
        if (useMobileIcons && input.get() == MouseInput.MOUSE_LEFT) {
            return MOBILE_TAP_MODE;
        }
        return input.get().getName().toLowerCase();
    }

    @Override
    public boolean isSkinAppliedByCanvas() {
        return false;
    }
}
