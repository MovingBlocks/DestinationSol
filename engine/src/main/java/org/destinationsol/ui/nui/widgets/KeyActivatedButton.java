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

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        if (isVisible() && isEnabled() && event.getKey() == getKey()) {
            if (event.getState() == ButtonState.UP) {
                activateWidget();
                setActive(false);
            } else if (event.getState() == ButtonState.DOWN) {
                setActive(true);
            }
            return true;
        }

        return super.onKeyEvent(event);
    }
}
