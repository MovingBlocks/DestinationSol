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

import org.terasology.input.MouseInput;
import org.terasology.math.geom.Vector2;
import org.destinationsol.nui.ActivatableWidget;
import org.destinationsol.nui.BaseInteractionListener;
import org.destinationsol.nui.Canvas;
import org.destinationsol.nui.InteractionListener;
import org.destinationsol.nui.TabbingManager;
import org.destinationsol.nui.databinding.Binding;
import org.destinationsol.nui.databinding.DefaultBinding;
import org.destinationsol.nui.events.NUIMouseClickEvent;

/**
 * A check-box. Hovering is supported.
 */
public class UICheckbox extends ActivatableWidget {
    public static final String HOVER_ACTIVE_MODE = "hover-active";

    private Binding<Boolean> active = new DefaultBinding<>(false);

    private InteractionListener interactionListener = new BaseInteractionListener() {

        @Override
        public boolean onMouseClick(NUIMouseClickEvent event) {
            if (event.getMouseButton() == MouseInput.MOUSE_LEFT) {
                activateWidget();
                return true;
            }
            return false;
        }

    };

    public UICheckbox() {
    }

    public UICheckbox(String id) {
        super(id);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isEnabled()) {
            canvas.addInteractionRegion(interactionListener);
        }
    }

    @Override
    public String getMode() {
        if (!isEnabled()) {
            return DISABLED_MODE;
        } else if (interactionListener.isMouseOver() || (TabbingManager.focusedWidget != null && TabbingManager.focusedWidget.equals(this))) {
            if (active.get()) {
                return HOVER_ACTIVE_MODE;
            }
            return HOVER_MODE;
        } else if (active.get()) {
            return ACTIVE_MODE;
        }
        return DEFAULT_MODE;
    }

    /**
     * @return A boolean indicating the status of the checkbox
     */
    public boolean isChecked() {
        return active.get();
    }

    /**
     * @param checked A boolean setting the ticked state of the checkbox
     */
    public void setChecked(boolean checked) {
        active.set(checked);
    }

    @Override
    public void activateWidget() {
        setChecked(!isChecked());
        UICheckbox.super.activateWidget();
    }


    public void bindChecked(Binding<Boolean> binding) {
        this.active = binding;
    }

    @Override
    public Vector2 getPreferredContentSize(Canvas canvas, Vector2 sizeHint) {
        return Vector2.zero();
    }

    /**
     * Subscribes a listener that is called whenever this {@code UICheckBox} is activated.
     *
     * @param listener The {@link ActivateEventListener} to be subscribed
     */
    public void subscribe(ActivateEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Unsubscribes a listener from this {@code UICheckBox}.
     *
     * @param listener The {@code ActivateEventListener}to be unsubscribed
     */
    public void unsubscribe(ActivateEventListener listener) {
        listeners.remove(listener);
    }
}
