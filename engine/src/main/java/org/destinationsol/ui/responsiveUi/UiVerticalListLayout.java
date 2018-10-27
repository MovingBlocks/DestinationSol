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
import java.util.ArrayList;
import java.util.List;
import org.destinationsol.SolApplication;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.SolInputManager;
import static org.destinationsol.ui.responsiveUi.UiTextButton.BUTTON_HEIGHT;
import static org.destinationsol.ui.responsiveUi.UiTextButton.BUTTON_PADDING;
import static org.destinationsol.ui.responsiveUi.UiTextButton.BUTTON_WIDTH;

// TODO: Only handles UiTextButtons perfectly for now, due to height calculations. Make it more generic.
public class UiVerticalListLayout implements UiElement {
    private List<UiElement> uiElements = new ArrayList<>();

    private int x;
    private int y;

    public UiVerticalListLayout addElement(UiElement uiElement) {
        uiElements.add(uiElement);

        calculateButtonPositions();

        return this;
    }

    @Override
    public UiVerticalListLayout setPosition(int x, int y) {
        this.x = x;
        this.y = y;

        calculateButtonPositions();

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
        return BUTTON_WIDTH;
    }

    @Override
    public int getHeight() {
        if (uiElements.isEmpty()) {
            return 0;
        }

        return uiElements.size() * BUTTON_HEIGHT + (uiElements.size() - 1) * BUTTON_PADDING;
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

    private void calculateButtonPositions() {
        int currentY = y - getHeight()/2 + BUTTON_HEIGHT/2;

        for (UiElement uiElement : uiElements) {
            uiElement.setPosition(x, currentY);

            currentY += (BUTTON_HEIGHT + BUTTON_PADDING);
        }
    }
}
