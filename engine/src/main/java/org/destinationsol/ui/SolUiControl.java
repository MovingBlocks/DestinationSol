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
    private final int[] myKeys;
    private final Rectangle myScreenArea;
    private final boolean myWithSound;
    private String myDisplayName;
    private boolean myEnabled = true;
    private boolean myKeyPressed;
    private boolean myKeyPressedPrev;
    private boolean myKeyFlash;
    private boolean myAreaPressed;
    private boolean myAreaFlash;
    private boolean myAreaJustUnpressed;
    private boolean myMouseHover;
    private int myWarnCount;

    public SolUiControl(Rectangle screenArea, boolean withSound, int... keys) {
        myWithSound = withSound;
        myKeys = keys == null ? new int[0] : keys;
        myScreenArea = screenArea;
    }

    public boolean maybeFlashPressed(int keyCode) {
        if (!myEnabled) {
            return false;
        }
        for (int key : myKeys) {
            if (key != keyCode) {
                continue;
            }
            myKeyFlash = true;
            return true;
        }
        return false;
    }

    public boolean maybeFlashPressed(SolInputManager.InputPointer inputPointer) {
        if (!myEnabled) {
            return false;
        }
        boolean pressed = myScreenArea != null && myScreenArea.contains(inputPointer.x, inputPointer.y);
        if (pressed) {
            myAreaFlash = true;
        }
        return pressed;
    }

    public void update(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, boolean canBePressed, SolInputManager inputMan,
                       SolApplication cmp) {
        if (!myEnabled) {
            canBePressed = false;
        }
        updateKeys(canBePressed);
        updateArea(inputPointers, canBePressed);
        updateHover(inputPointers, cursorShown, inputMan, cmp);
        if (myWithSound && isJustOff()) {
            inputMan.playClick(cmp);
        }
        if (myWarnCount > 0) {
            myWarnCount--;
        }
    }

    private void updateHover(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, SolInputManager inputMan, SolApplication cmp) {
        if (myScreenArea == null || myAreaPressed || inputPointers[0].pressed) {
            return;
        }
        boolean prev = myMouseHover;
        myMouseHover = cursorShown && myScreenArea.contains(inputPointers[0].x, inputPointers[0].y);
        if (myWithSound && myMouseHover && !prev) {
            inputMan.playHover(cmp);
        }
    }

    private void updateKeys(boolean canBePressed) {
        myKeyPressedPrev = myKeyPressed;
        if (myKeyFlash) {
            myKeyPressed = true;
            myKeyFlash = false;
        } else {
            myKeyPressed = false;
            if (canBePressed) {
                for (int key : myKeys) {
                    if (!Gdx.input.isKeyPressed(key)) {
                        continue;
                    }
                    myKeyPressed = true;
                    break;
                }
            }
        }
    }

    private void updateArea(SolInputManager.InputPointer[] inputPointers, boolean canBePressed) {
        if (myScreenArea == null) {
            return;
        }
        myAreaJustUnpressed = false;
        if (myAreaFlash) {
            myAreaPressed = true;
            myAreaFlash = false;
        } else {
            myAreaPressed = false;
            if (canBePressed) {
                for (SolInputManager.InputPointer inputPointer : inputPointers) {
                    if (!myScreenArea.contains(inputPointer.x, inputPointer.y)) {
                        continue;
                    }
                    myAreaPressed = inputPointer.pressed;
                    myAreaJustUnpressed = !inputPointer.pressed && inputPointer.prevPressed;
                    break;
                }
            }
        }
    }

    // poll to perform continuous actions
    public boolean isOn() {
        return myEnabled && (myKeyPressed || myAreaPressed);
    }

    // poll to perform one-off actions
    public boolean isJustOff() {
        return myEnabled && (!myKeyPressed && myKeyPressedPrev || myAreaJustUnpressed);
    }

    public String getDisplayName() {
        return myDisplayName;
    }

    public void setDisplayName(String displayName) {
        myDisplayName = displayName;
    }

    public void drawButton(UiDrawer uiDrawer, SolApplication cmp, Color warnCol) {
        if (myScreenArea == null) {
            return;
        }
        Color tint = SolColor.UI_INACTIVE;
        if (myEnabled) {
            if (isOn()) {
                tint = SolColor.UI_LIGHT;
            } else if (myMouseHover) {
                tint = SolColor.UI_MED;
            } else {
                tint = SolColor.UI_DARK;
            }
        }
        uiDrawer.draw(myScreenArea, tint);
        if (myWarnCount > 0) {
            uiDrawer.draw(myScreenArea, warnCol);
        }
    }

    public void drawDisplayName(UiDrawer uiDrawer) {
        if (myScreenArea == null) {
            return;
        }
        Color tint = myEnabled ? SolColor.WHITE : SolColor.G;
        uiDrawer.drawString(myDisplayName, myScreenArea.x + myScreenArea.width / 2, myScreenArea.y + myScreenArea.height / 2,
                FontSize.MENU, true, tint);
    }

    public void blur() {
        myKeyPressed = false;
        myKeyPressedPrev = false;
        myAreaPressed = false;
        myAreaJustUnpressed = false;
    }

    public boolean isEnabled() {
        return myEnabled;
    }

    public void setEnabled(boolean enabled) {
        myEnabled = enabled;
    }

    public Rectangle getScreenArea() {
        return myScreenArea;
    }

    public boolean isMouseHover() {
        return myMouseHover;
    }

    public void enableWarn() {
        myWarnCount = 2;
    }
}
