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
import com.google.common.collect.ObjectArrays;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.FontSize;
import org.destinationsol.ui.SolInputManager;
import org.destinationsol.ui.UiDrawer;

import java.util.ArrayList;

import static org.destinationsol.ui.responsiveUi.UiTextButton.BUTTON_HEIGHT;
import static org.destinationsol.ui.responsiveUi.UiTextButton.BUTTON_WIDTH;

public class UiDropdownList implements UiElement, UiDrawOnTopComponent {

    private Rectangle screenArea;
    private Rectangle hoverArea;
    private ArrayList<Rectangle> screenAreas;

    private String[] options;
    private String currentSelection;

    private UiCallback changeAction;

    private boolean isAreaJustUnpressed;
    private boolean open;
    private boolean hover;

    private int x;
    private int y;
    private int width = BUTTON_WIDTH;
    private int height = BUTTON_HEIGHT;

    public UiDropdownList addOptions(String[] displayNames) {
        if (options == null) {
            options = displayNames;
        } else {
            options = ObjectArrays.concat(options, displayNames, String.class);
        }
        currentSelection = displayNames[0];
        return this;
    }

    public UiDropdownList setSelectionChangeAction(UiCallback selectionChangeAction) {
        changeAction = selectionChangeAction;
        return this;
    }

    public String getCurrentSelection() {
        return currentSelection;
    }

    @Override
    public UiDropdownList setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        open = false;

        DisplayDimensions displayDimensions = SolApplication.displayDimensions;

        float screenAreaX = (x - width / 2) * displayDimensions.getRatio() / displayDimensions.getWidth();
        float screenAreaY = (y - height / 2) / (float) displayDimensions.getHeight();
        float screenAreaWidth = width * displayDimensions.getRatio() / displayDimensions.getWidth();
        float screenAreaHeight = height / (float) displayDimensions.getHeight();
        float screenAreaYMultiplier = height / (float) displayDimensions.getHeight();

        screenArea = new Rectangle(screenAreaX, screenAreaY, screenAreaWidth, screenAreaHeight);

        if (screenAreas == null) {
            screenAreas = new ArrayList<>();
        } else {
            screenAreas.clear();
        }
        for (int i = 0; i < options.length; i++) {
            screenAreas.add(new Rectangle(screenAreaX, screenAreaY + i * screenAreaYMultiplier, screenAreaWidth, screenAreaHeight));
        }

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
    public boolean maybeFlashPressed(int keyCode) {
        return false;
    }

    @Override
    public boolean maybeFlashPressed(SolInputManager.InputPointer inputPointer) {
        return false;
    }

    @Override
    public boolean update(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, boolean canBePressed, SolInputManager inputMan, SolApplication cmp) {

        isAreaJustUnpressed = false;
        if (canBePressed) {
            for (SolInputManager.InputPointer inputPointer : inputPointers) {
                if (screenArea.contains(inputPointer.x, inputPointer.y)) {
                    isAreaJustUnpressed = !inputPointer.pressed && inputPointer.prevPressed;
                    break;
                }
            }
        }

        if (isAreaJustUnpressed) {
            open = !open;

            if (open) {
                screenArea.height *= options.length;
            } else {
                for (int i = 0; i < screenAreas.size(); i++) {
                    if (screenAreas.get(i).contains(inputPointers[0].x, inputPointers[0].y)) {
                        currentSelection = options[i];
                        break;
                    }
                }
                if (changeAction != null) {
                    changeAction.callback(this);
                }
                screenArea.height /= options.length;
            }
            return true;
        }

        hover = !inputPointers[0].pressed && cursorShown && screenArea.contains(inputPointers[0].x, inputPointers[0].y);

        if (hover) {
            for (Rectangle area : screenAreas) {
                if (area.contains(inputPointers[0].x, inputPointers[0].y)) {
                    hoverArea = area;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void draw() {
        if (screenArea == null) {
            return;
        }

        UiDrawer uiDrawer = SolApplication.getUiDrawer();

        float stringX = screenArea.x + screenArea.width / 2;

        if (open) {
            if (hover) {
                uiDrawer.draw(hoverArea, SolColor.UI_OPAQUE.cpy().add(0.05f, 0.05f, 0.05f, 0));
                for (Rectangle area : screenAreas) {
                    if (area.y != hoverArea.y) {
                        uiDrawer.draw(area, SolColor.UI_OPAQUE);
                    }
                }
            } else {
                uiDrawer.draw(screenArea, SolColor.UI_OPAQUE);
            }
            for (int i = 0; i < options.length; i++) {
                uiDrawer.drawString(options[i], stringX, screenAreas.get(i).y + screenAreas.get(i).height / 2, FontSize.MENU, true, SolColor.WHITE);
            }
        } else {
            if (hover) {
                uiDrawer.draw(screenArea, SolColor.UI_MED);
            } else {
                uiDrawer.draw(screenArea, SolColor.UI_DARK);
            }
            uiDrawer.drawString(currentSelection, stringX, screenArea.y + screenArea.height / 2, FontSize.MENU, true, SolColor.WHITE);
        }
    }

    @Override
    public void blur() {

    }

    @Override
    public Rectangle getScreenArea() {
        return screenArea;
    }
}
