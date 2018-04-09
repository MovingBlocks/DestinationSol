/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;

public class SolUiControl {
    private final int[] keys;
    private final Rectangle screenArea;
    private final boolean isWithSound;
    private String displayName;
    private boolean isEnabled = true;
    private boolean isKeyPressed;
    private boolean wasKeyPressed;
    private boolean isKeyFlashed;
    private boolean isAreaPressed;
    private boolean isAreaFlashed;
    private boolean isAreaJustUnpressed;
    private boolean doesMouseHover;
    private int warnCount;

    public SolUiControl(Rectangle screenArea, boolean isWithSound, int... keys) {
        this.isWithSound = isWithSound;
        this.keys = keys == null ? new int[0] : keys;
        this.screenArea = screenArea;
    }

    public boolean maybeFlashPressed(int keyCode) {
        if (!isEnabled) {
            return false;
        }
        for (int key : keys) {
            if (key != keyCode) {
                continue;
            }
            isKeyFlashed = true;
            return true;
        }
        return false;
    }

    public boolean maybeFlashPressed(SolInputManager.InputPointer inputPointer) {
        if (!isEnabled) {
            return false;
        }
        boolean pressed = screenArea != null && screenArea.contains(inputPointer.x, inputPointer.y);
        if (pressed) {
            isAreaFlashed = true;
        }
        return pressed;
    }

    public void update(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, boolean canBePressed, SolInputManager inputMan,
                       SolApplication cmp) {
        if (!isEnabled) {
            canBePressed = false;
        }
        updateKeys(canBePressed);
        updateArea(inputPointers, canBePressed);
        updateHover(inputPointers, cursorShown, inputMan, cmp);
        if (isWithSound && isJustOff()) {
            inputMan.playClick(cmp);
        }
        if (warnCount > 0) {
            warnCount--;
        }
    }

    private void updateHover(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, SolInputManager inputMan, SolApplication cmp) {
        if (screenArea == null || isAreaPressed || inputPointers[0].pressed) {
            return;
        }
        boolean prev = doesMouseHover;
        doesMouseHover = cursorShown && screenArea.contains(inputPointers[0].x, inputPointers[0].y);
        if (isWithSound && doesMouseHover && !prev) {
            inputMan.playHover(cmp);
        }
    }

    private void updateKeys(boolean canBePressed) {
        wasKeyPressed = isKeyPressed;
        if (isKeyFlashed) {
            isKeyPressed = true;
            isKeyFlashed = false;
        } else {
            isKeyPressed = false;
            if (canBePressed) {
                for (int key : keys) {
                    if (!Gdx.input.isKeyPressed(key)) {
                        continue;
                    }
                    isKeyPressed = true;
                    break;
                }
            }
        }
    }

    private void updateArea(SolInputManager.InputPointer[] inputPointers, boolean canBePressed) {
        if (screenArea == null) {
            return;
        }
        isAreaJustUnpressed = false;
        if (isAreaFlashed) {
            isAreaPressed = true;
            isAreaFlashed = false;
        } else {
            isAreaPressed = false;
            if (canBePressed) {
                for (SolInputManager.InputPointer inputPointer : inputPointers) {
                    if (!screenArea.contains(inputPointer.x, inputPointer.y)) {
                        continue;
                    }
                    isAreaPressed = inputPointer.pressed;
                    isAreaJustUnpressed = !inputPointer.pressed && inputPointer.prevPressed;
                    break;
                }
            }
        }
    }

    // poll to perform continuous actions
    public boolean isOn() {
        return isEnabled && (isKeyPressed || isAreaPressed);
    }

    // poll to perform one-off actions
    public boolean isJustOff() {
        return isEnabled && (!isKeyPressed && wasKeyPressed || isAreaJustUnpressed);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void drawButton(UiDrawer uiDrawer, SolApplication cmp, Color warnCol) {
        if (screenArea == null) {
            return;
        }
        Color tint = SolColor.UI_INACTIVE;
        if (isEnabled) {
            if (isOn()) {
                tint = SolColor.UI_LIGHT;
            } else if (doesMouseHover) {
                tint = SolColor.UI_MED;
            } else {
                tint = SolColor.UI_DARK;
            }
        }
        uiDrawer.draw(screenArea, tint);
        if (warnCount > 0) {
            uiDrawer.draw(screenArea, warnCol);
        }
    }

    public void drawDisplayName(UiDrawer uiDrawer) {
        if (screenArea == null) {
            return;
        }
        Color tint = isEnabled ? SolColor.WHITE : SolColor.G;
        uiDrawer.drawString(displayName, screenArea.x + screenArea.width / 2, screenArea.y + screenArea.height / 2,
                FontSize.MENU, true, tint);
    }

    public void blur() {
        isKeyPressed = false;
        wasKeyPressed = false;
        isAreaPressed = false;
        isAreaJustUnpressed = false;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Rectangle getScreenArea() {
        return screenArea;
    }

    public boolean isMouseHover() {
        return doesMouseHover;
    }

    public void enableWarn() {
        warnCount = 2;
    }
}
