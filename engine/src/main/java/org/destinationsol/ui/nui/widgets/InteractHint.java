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

public class InteractHint extends CoreWidget {
    public static String MOBILE_TAP_MODE = "mobile_tap";
    private Binding<Input> input = new DefaultBinding<>(Keyboard.Key.NONE);
    @LayoutConfig
    private boolean useMobileIcons;

    public InteractHint() {
    }

    public InteractHint(String id, Input input) {
        setId(id);
        this.input.set(input);
    }

    public Input getInput() {
        return input.get();
    }

    public void setInput(Input input) {
        this.input.set(input);
    }

    public void bindInput(Binding<Input> input) {
        this.input = input;
    }

    public boolean isUsingMobileIcons() {
        return useMobileIcons;
    }

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
