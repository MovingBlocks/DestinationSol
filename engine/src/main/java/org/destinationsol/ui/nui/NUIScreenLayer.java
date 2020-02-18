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

import org.terasology.input.ButtonState;
import org.terasology.input.Keyboard;
import org.terasology.math.geom.Vector2i;
import org.terasology.nui.AbstractWidget;
import org.terasology.nui.Canvas;
import org.terasology.nui.FocusManager;
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.UIWidget;
import org.terasology.nui.events.NUIKeyEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public abstract class NUIScreenLayer extends AbstractWidget {
    @LayoutConfig
    protected UIWidget contents;
    protected FocusManager focusManager;
    protected NUIManager nuiManager;

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawWidget(contents);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        contents.update(delta);
    }

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

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        if (escapeCloses() && event.getState() == ButtonState.UP && event.getKey() == Keyboard.Key.ESCAPE) {
            nuiManager.removeScreen(this);
            return true;
        }

        return super.onKeyEvent(event);
    }

    public void initialise() {
    }

    public void onRemoved() {
    }

    public boolean isBlockingInput() {
        return false;
    }

    protected boolean escapeCloses() {
        return true;
    }

    void setFocusManager(FocusManager focusManager) {
        this.focusManager = focusManager;
    }

    protected FocusManager getFocusManager() {
        return focusManager;
    }

    void setNuiManager(NUIManager nuiManager) {
        this.nuiManager = nuiManager;
    }

    protected NUIManager getNuiManager() {
        return nuiManager;
    }
}
