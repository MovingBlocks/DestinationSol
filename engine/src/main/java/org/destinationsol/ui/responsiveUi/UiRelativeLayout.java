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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UiRelativeLayout extends AbstractUiElement {
    private List<UiElementWithProperties> uiElementsWithProperties = new ArrayList<>();

    public UiRelativeLayout addElement(UiElement uiElement, UiPosition referencePosition, int xOffset, int yOffset) {
        UiElementWithProperties uiElementWithProperties = new UiElementWithProperties(uiElement, referencePosition, xOffset, yOffset);
        setPosition(uiElementWithProperties);
        uiElementsWithProperties.add(uiElementWithProperties);

        return this;
    }

    public UiRelativeLayout addHeadlessElement(UiElement uiElement) {
        return addElement(uiElement, null, 0, 0);
    }

    @Override
    public UiRelativeLayout setPosition(int x, int y) {
        for (UiElementWithProperties uiElementWithProperties : uiElementsWithProperties) {
            setPosition(uiElementWithProperties);
        }

        return this;
    }

    @Override
    public UiRelativeLayout setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
        return this;
    }

    @Override
    public int getX() {
        return -1;
    }

    @Override
    public int getY() {
        return -1;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    private void setPosition(UiElementWithProperties uiElementWithProperties) {
        if (uiElementWithProperties.referencePosition == null) {
            return;
        }

        int uiElementX = uiElementWithProperties.referencePosition.getX() + uiElementWithProperties.xOffset;
        int uiElementY = uiElementWithProperties.referencePosition.getY() + uiElementWithProperties.yOffset;

        uiElementWithProperties.uiElement.setPosition(uiElementX, uiElementY);
    }

    public void draw() {
        for (UiElementWithProperties uiElementWithProperties : uiElementsWithProperties) {
            uiElementWithProperties.uiElement.draw();
        }
    }

    @Override
    public boolean maybeFlashPressed(int keyCode) {
        for (UiElementWithProperties uiElementWithProperties : uiElementsWithProperties) {
            if (uiElementWithProperties.uiElement.maybeFlashPressed(keyCode)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean maybeFlashPressed(SolInputManager.InputPointer inputPointer) {
        for (UiElementWithProperties uiElementWithProperties : uiElementsWithProperties) {
            if (uiElementWithProperties.uiElement.maybeFlashPressed(inputPointer)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean update(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, boolean canBePressed, SolInputManager inputMan, SolApplication cmp) {
        boolean consumed = false;

        for (UiElementWithProperties uiElementWithProperties : uiElementsWithProperties) {
            if (uiElementWithProperties.uiElement.update(inputPointers, cursorShown, canBePressed, inputMan, cmp)) {
                consumed = true;
            }
        }

        return consumed;
    }

    @Override
    public Rectangle getScreenArea() {
        return new Rectangle(-1, -1, 0, 0);
    }

    @Override
    public void blur() {
        for (UiElementWithProperties uiElementWithProperties : uiElementsWithProperties) {
            uiElementWithProperties.uiElement.blur();
        }
    }

    public class UiElementWithProperties {
        private UiElement uiElement;
        private UiPosition referencePosition;
        private int xOffset;
        private int yOffset;

        UiElementWithProperties(UiElement uiElement, UiPosition referencePosition, int xOffset, int yOffset) {
            this.uiElement = uiElement;
            this.referencePosition = referencePosition;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
