/*
 * Copyright 2020 The Terasology Foundation
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
package org.destinationsol.ui.nui;

import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.joml.Vector2i;
import org.terasology.input.ButtonState;
import org.terasology.input.Keyboard;
import org.terasology.nui.AbstractWidget;
import org.terasology.nui.Canvas;
import org.terasology.nui.FocusManager;
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.UIWidget;
import org.terasology.nui.events.NUIKeyEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

/**
 * A specialised {@link UIWidget} designed to contain the widgets comprising a single UI screen (or screen layer).
 */
public abstract class NUIScreenLayer extends AbstractWidget {
    /**
     * The contents of the UI screen. It contains all of the widgets in the screen and can be queried with the
     * {@link #find} method.
     */
    @LayoutConfig
    protected UIWidget contents;
    /**
     * The focus manager assigned to the UI screen.
     */
    protected FocusManager focusManager;
    /**
     * The game's NUI Manager.
     */
    protected NUIManager nuiManager;

    /**
     * This should just render the widgets contained in the UI screen, however it can be overridden to render custom
     * graphics as well.
     * @param canvas the canvas to draw on
     */
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawWidget(contents);
    }

    /**
     * This updates the widgets contained in the UI screen. It is called every cycle and can therefore be used for
     * updating the contents of the widgets as well.
     * @param delta the time elapsed since the last update cycle
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        contents.update(delta);
    }

    /**
     * UI Screens can not by-default become focused themselves, as the focus should be on their contents instead.
     * @return false, by default
     */
    @Override
    public boolean canBeFocus() {
        return false;
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
        return sizeHint;
    }

    /**
     * UI Screens can potentially occupy any size, as their contents is not restricted.
     * @param canvas the canvas to render to
     * @return the maximum possible size (Integer.MAX_VALUE, Integer.MAX_VALUE).
     */
    @Override
    public Vector2i getMaxContentSize(Canvas canvas) {
        return new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<UIWidget> iterator() {
        if (contents == null) {
            return Collections.emptyIterator();
        }
        return Arrays.asList(contents).iterator();
    }

    /**
     * This manages by-default the escape key closing the UI screen, however it can be overridden for more specific
     * behaviour, such (for example) as pressing a certain key to trigger a sub-menu.
     * @param event the key event generated
     * @return true, if the event should be consumed, otherwise false
     */
    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        if (escapeCloses() && event.getState() == ButtonState.UP && event.getKey() == Keyboard.Key.ESCAPE) {
            nuiManager.removeScreen(this);
            return true;
        }

        // Send key events to all KeyActivatedButton sub-widgets. These buttons are supposed to react to key events
        // even when they are not in-focus.
        for (UIWidget widget : contents.findAll(KeyActivatedButton.class)) {
            widget.onKeyEvent(event);
        }

        return super.onKeyEvent(event);
    }

    /**
     * Called to initialise the UI screen, allowing it to register widget callbacks and assign widget values
     * programmatically. This is called whenever the UI screen becomes visible.
     */
    public void initialise() {
    }

    /**
     * Called when the UI screen is removed from display.
     */
    public void onRemoved() {
    }

    /**
     * States if the inputs received by this UI screen should not be received by other UI screens underneath.
     * @return true if inputs should be blocked, otherwise false
     */
    public boolean isBlockingInput() {
        return false;
    }

    /**
     * Is this UI screen closed when the escape key is pressed?
     * @return true, if the screen should be closed
     */
    protected boolean escapeCloses() {
        return true;
    }

    /**
     * Sets the focus manager to use.
     * Primary usage is in {@link NUIManager#pushScreen}
     * @param focusManager the focus manager to use.
     */
    void setFocusManager(FocusManager focusManager) {
        this.focusManager = focusManager;
    }

    /**
     * Returns the focus manager currently assigned to this UI screen.
     * @return the currently assigned focus manager
     */
    protected FocusManager getFocusManager() {
        return focusManager;
    }

    /**
     * Sets the game's NUI Manager.
     * @param nuiManager the game's NUI Manager
     */
    void setNuiManager(NUIManager nuiManager) {
        this.nuiManager = nuiManager;
    }

    /**
     * Returns the game's NUI Manager.
     * @return the game's NUI Manager
     */
    protected NUIManager getNuiManager() {
        return nuiManager;
    }
}
