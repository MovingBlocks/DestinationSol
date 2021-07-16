package org.destinationsol.ui.nui.widgets;

import org.terasology.input.ButtonState;
import org.terasology.input.Input;
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
    private Binding<String> key = new DefaultBinding<>(Keyboard.Key.NONE.getName());

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
     * Retrieves the key used to activate this {@code KeyActivatedButton}.
     *
     * @return The key used to activate this {@code KeyActivatedButton}
     */
    public Input getKey() {
        return Keyboard.Key.find(key.get());
    }

    /**
     * Sets the key used to activate this {@code KeyActivatedButton}.
     *
     * @param key The key used to activate this {@code KeyActivatedButton}
     */
    public void setKey(Keyboard.Key key) {
        this.key.set(key.getName());
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

        return super.onKeyEvent(event);
    }
}
