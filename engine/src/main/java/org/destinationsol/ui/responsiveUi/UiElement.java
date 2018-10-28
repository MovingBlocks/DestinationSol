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
package org.destinationsol.ui.responsiveUi;

import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.SolApplication;
import org.destinationsol.ui.SolInputManager;

import java.util.Optional;

public interface UiElement {
    /**
     * Sets position of this element.
     *
     * @param x X position of the element
     * @param y Y position of the element.
     * @return Self for method chaining.
     */
    UiElement setPosition(int x, int y);

    /**
     * Used to manually trigger recalculation of the element.
     *
     * May be used when something was changed in uncommon way that might require change of the element, or by children
     * to notify their parent of their change that might require action from the parent.
     * @return Self for method chaining.
     */
    default UiElement recalculate() {
        return this;
    }

    /**
     * Gets parent {@link UiContainerElement element} of this element.
     * @return Parent element, or {@link Optional#empty()} if topmost element.
     */
    Optional<UiContainerElement> getParent();

    /**
     * Sets parent {@link UiContainerElement} to this element.
     *
     * {@link UiContainerElement#addElement(UiElement)} is responsible of calling this method.
     * @param parent Parent to set
     * @return Self for method chaining
     */
    UiElement setParent(UiContainerElement parent);

    /**
     * Returns x position of this element.
     *
     * The position should point to the center of this element.
     * @return X position of this element
     */
    int getX();

    /**
     * Returns y position of this element.
     *
     * The position should point to the center of this element.
     * @return Y position of this element
     */
    int getY();

    int getWidth();

    int getHeight();

    void draw();

    // TODO: Ugly, ugly, ugly. Remove.
    boolean maybeFlashPressed(int keyCode);

    // TODO: Ugly, ugly, ugly. Remove.
    boolean maybeFlashPressed(SolInputManager.InputPointer inputPointer);

    // TODO: Ugly, ugly, ugly. Remove.
    boolean update(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, boolean canBePressed, SolInputManager inputMan, SolApplication cmp);

    // TODO: Ugly, ugly, ugly. Remove.
    Rectangle getScreenArea();

    // TODO: Ugly, ugly, ugly. Remove.
    /**
     * Called when hiding this element.
     */
    void blur();
}
