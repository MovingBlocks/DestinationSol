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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import org.destinationsol.SolApplication;
import org.destinationsol.ui.SolInputManager;

import java.util.Optional;

public class UiHeadlessButton implements UiElement {
    private int triggerKey;
    private boolean isEnabled = true;

    private boolean isKeyPressed;
    private boolean wasKeyPressed;
    private boolean isKeyFlashed;
    private boolean isAreaPressed;
    private boolean isAreaJustUnpressed;

    // TODO: Make these optional?
    private UiCallback onClickAction; // Called *while* button is pressed
    private UiCallback onReleaseAction; // Called when button is released
    private Optional<UiContainerElement> parent;

    @Override
    public UiHeadlessButton setPosition(int x, int y) {
        return this;
    }

    @Override
    public Optional<UiContainerElement> getParent() {
        return parent;
    }

    @Override
    public UiHeadlessButton setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
        return this;
    }

    public UiHeadlessButton setTriggerKey(int triggerKey) {
        this.triggerKey = triggerKey;

        return this;
    }

    public UiHeadlessButton setOnClickAction(UiCallback onClickAction) {
        this.onClickAction = onClickAction;

        return this;
    }

    public UiHeadlessButton setOnReleaseAction(UiCallback onReleaseAction) {
        this.onReleaseAction = onReleaseAction;

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

    @Override
    public boolean maybeFlashPressed(int keyCode) {
        if (!isEnabled) {
            return false;
        }

        if (triggerKey == keyCode) {
            isKeyFlashed = true;
            return true;
        }

        // TODO: Not present in original implementation. Examine why it wasn't, and look at consequences of adding it.
        // isKeyFlashed = false;
        return false;
    }

    @Override
    public boolean maybeFlashPressed(SolInputManager.InputPointer inputPointer) {
        return false;
    }

    @Override
    public boolean update(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, boolean canBePressed, SolInputManager inputMan, SolApplication cmp) {
        if (!isEnabled) {
            canBePressed = false;
        }
        updateKeys(canBePressed);

        if (isOn()) {
            if (onClickAction != null) {
                onClickAction.callback(this);
            }
        } else if (isJustOff()) {
            if (onReleaseAction != null) {
                onReleaseAction.callback(this);
            }
        }

        return (isOn() || isJustOff());
    }

    private void updateKeys(boolean canBePressed) {
        wasKeyPressed = isKeyPressed;
        if (isKeyFlashed) {
            isKeyPressed = true;
            isKeyFlashed = false;
        } else {
            if (canBePressed) {
                isKeyPressed = Gdx.input.isKeyPressed(triggerKey);
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

    @Override
    public void draw() { }

    @Override
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

    @Override
    public Rectangle getScreenArea() {
        return new Rectangle(-1, -1, 0, 0);
    }
}
