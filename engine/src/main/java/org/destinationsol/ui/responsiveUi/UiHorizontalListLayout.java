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

import org.destinationsol.SolApplication;
import org.destinationsol.ui.SolInputManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UiHorizontalListLayout extends AbstractUiElement implements UiContainerElement {
    private List<UiElement> uiElements = new ArrayList<>();

    private int x;
    private int y;
    private int width = 0;
    private int height = 0;
    private int padding = UiConstants.DEFAULT_ELEMENT_PADDING;

    public UiHorizontalListLayout setPadding(int padding) {
        this.padding = padding;
        return this;
    }

    @Override
    public UiHorizontalListLayout addElement(UiElement uiElement) {
        uiElements.add(uiElement);
        uiElement.setParent(this);
        recalculateInnerPositions();

        return this;
    }

    @Override
    public UiHorizontalListLayout setPosition(int x, int y) {
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
    public void blur() {
        for (UiElement uiElement : uiElements) {
            uiElement.blur();
        }
    }

    @Override
    public UiHorizontalListLayout recalculate() {
        recalculateInnerPositions();
        return this;
    }

    @Override
    public UiHorizontalListLayout setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
        return this;
    }

    private void recalculateInnerPositions() {
        width = 0;
        height = 0;
        for (UiElement uiElement : uiElements) {
            if (uiElement instanceof UiResizableElement) {
                if (((UiResizableElement) uiElement).getDefaultHeight() > height) {
                    height = ((UiResizableElement) uiElement).getDefaultHeight();
                }
            } else {
                if (uiElement.getHeight() > height) {
                    height = uiElement.getHeight();
                }
            }
            width += uiElement.getWidth();
        }
        width += (uiElements.size() - 1) * padding;


        int leftX = x - (width / 2);
        for (UiElement uiElement: uiElements) {
            if (uiElement instanceof UiResizableElement) {
                ((UiResizableElement) uiElement).setHeight(height);
            }
            uiElement.setPosition(leftX + (uiElement.getWidth() / 2), y);
            leftX += uiElement.getWidth() + padding;
        }
    }
}
