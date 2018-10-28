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
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.SolInputManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO: Only handles UiTextButtons perfectly for now, due to height calculations. Make it more generic.
public class UiVerticalListLayout implements UiElement, UiContainerElement {
    private List<UiElement> uiElements = new ArrayList<>();

    private int x;
    private int y;
    private int width;
    private int height = 0;
    private Optional<UiContainerElement> parent;

    @Override
    public UiVerticalListLayout addElement(UiElement uiElement) {
        uiElements.add(uiElement);
        uiElement.setParent(this);
        recalculateInnerPositions();

        return this;
    }

    @Override
    public UiVerticalListLayout setPosition(int x, int y) {
        this.x = x;
        this.y = y;

        recalculateInnerPositions();

        return this;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void draw() {
        for (UiElement uiElement : uiElements) {
            uiElement.draw();
        }
    }

    @Override
    public boolean maybeFlashPressed(int keyCode) {
        for (UiElement uiElement : uiElements) {
            if (uiElement.maybeFlashPressed(keyCode)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean maybeFlashPressed(SolInputManager.InputPointer inputPointer) {
        for (UiElement uiElement : uiElements) {
            if (uiElement.maybeFlashPressed(inputPointer)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean update(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, boolean canBePressed, SolInputManager inputMan, SolApplication cmp) {
        boolean consumed = false;

        for (UiElement uiElement : uiElements) {
            if (uiElement.update(inputPointers, cursorShown, canBePressed, inputMan, cmp)) {
                consumed = true;
            }
        }

        return consumed;
    }

    @Override
    public Rectangle getScreenArea() {
        // TODO: Potentially problematic.
        DisplayDimensions displayDimensions = SolApplication.displayDimensions;
        return new Rectangle((x - getWidth()/2) * displayDimensions.getRatio() / displayDimensions.getWidth(), (y - getHeight()/2) / (float)displayDimensions.getHeight(), getWidth() * displayDimensions.getRatio() / displayDimensions.getWidth(), getHeight() / (float)displayDimensions.getHeight());
    }

    @Override
    public void blur() {
        for (UiElement uiElement : uiElements) {
            uiElement.blur();
        }
    }

    @Override
    public UiVerticalListLayout recalculate() {
        recalculateInnerPositions();
        return this;
    }

    @Override
    public Optional<UiContainerElement> getParent() {
        return parent;
    }

    @Override
    public UiVerticalListLayout setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
        return this;
    }

    private void recalculateInnerPositions() {
        height = 0;
        width = 0;
        for (UiElement uiElement : uiElements) {
            if (uiElement instanceof UiResizableElement) {
                if (((UiResizableElement) uiElement).getDefaultWidth() > width) {
                    width = ((UiResizableElement) uiElement).getDefaultWidth();
                }
            } else {
                if (uiElement.getWidth() > width) {
                    width = uiElement.getWidth();
                }
            }
            height += uiElement.getHeight();
        }
        height += (uiElements.size() - 1) * UiConstants.DEFAULT_ELEMENT_PADDING;

        int topY = y - (height / 2);
        for (UiElement uiElement: uiElements) {
            if (uiElement instanceof UiResizableElement) {
                ((UiResizableElement) uiElement).setWidth(width);
            }
            uiElement.setPosition(x, topY + (uiElement.getHeight() / 2));
            topY += uiElement.getHeight() + UiConstants.DEFAULT_ELEMENT_PADDING;
        }
    }
}
