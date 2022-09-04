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

import org.terasology.input.Keyboard;
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.databinding.Binding;
import org.terasology.nui.databinding.DefaultBinding;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.widgets.UIButton;

/**
 * A {@link UIButton} this can also be activated when a specific key is pressed.
 */
public class KeyActivatedButton extends UIButton {
    /**
     * The {@code Binding} containing the key that activates this button.
     */
    @LayoutConfig
    private Binding<Keyboard.Key> key = new DefaultBinding<>(Keyboard.Key.NONE);

    /**
     * The {@code Binding} stating if the button should still activate on key presses when invisible.
     * The default value is false, as this is consistent with the default behaviour of a UIButton.
     *
     * Setting the value to true allows the button to be activated by a key press when invisible,
     * which is consistent with the default behaviour of {@link org.destinationsol.ui.SolUiControl}.
     */
    @LayoutConfig
    private Binding<Boolean> activateWhenInvisible = new DefaultBinding<>(false);

    /**
     * Binds the key used to activate this {@code KeyActivatedButton}.
     *
     * @param key The {@code Binding} containing the key used to activate this {@code KeyActivatedButton}.
     */
    public void bindKey(Binding<Keyboard.Key> key) {
        this.key = key;
    }

    /**
     * Retrieves the key used to activate this {@code KeyActivatedButton}.
     *
     * @return The key used to activate this {@code KeyActivatedButton}
     */
    public Keyboard.Key getKey() {
        return this.key.get();
    }

    /**
     * Sets the key used to activate this {@code KeyActivatedButton}.
     *
     * @param key The key used to activate this {@code KeyActivatedButton}
     */
    public void setKey(Keyboard.Key key) {
        this.key.set(key);
    }

    /**
     * Returns true if the button should be activated by key presses when invisible.
     * @return true, if the button should be activated by key presses when invisible, otherwise false
     */
    // This method should really be called "shouldActivateWhenInvisible" but the widget de-serialisation logic
    // seems to require get* and set* method names to populate the bindings.
    public boolean getActivateWhenInvisible() {
        return activateWhenInvisible.get();
    }

    /**
     * Sets if the button should be activated by key presses when invisible.
     * @param value true, if the button should be activated by key presses when invisible, otherwise false
     */
    public void setActivateWhenInvisible(boolean value) {
        activateWhenInvisible.set(value);
    }

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        if (event.getKey() == getKey()) {
            if ((!isVisible() && !activateWhenInvisible.get()) || !isEnabled()) {
                setActive(false);
                return super.onKeyEvent(event);
            }

            if (!event.isDown()) {
                activateWidget();
                setActive(false);
            } else {
                setActive(true);
            }
            return true;
        }

        // WidgetWithOrder contains some logic that consumed all UP key and DOWN key events, even when it does nothing
        // with them. This might be worthwhile for scrolling lists but shouldn't do anything otherwise.
        if (parent == null) {
            return false;
        }
        return super.onKeyEvent(event);
    }

    /**
     * Simulates a button press, activating the widget immediately.
     * This can be used for unconventional input triggers, such as mouse wheel moves,
     * which are mapped as auxiliary triggers for the button.
     */
    public void simulatePress() {
        activateWidget();
    }
}
