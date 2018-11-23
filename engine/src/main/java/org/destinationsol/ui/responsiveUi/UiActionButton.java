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
import com.badlogic.gdx.graphics.Color;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.SolInputManager;

import java.util.Optional;
import java.util.stream.Stream;

import static org.destinationsol.ui.responsiveUi.UiConstants.DEFAULT_BUTTON_PADDING;

//TODO implement warn. Consider using timer for that to not have it to be retriggered externally.
public class UiActionButton extends AbstractUiElement implements UiResizableElement, UiContainerElement {
    public static final int DEFAULT_BUTTON_WIDTH = 300;
    public static final int DEFAULT_BUTTON_HEIGHT = 75;
    private UiElement containedElement;
    private int padding = DEFAULT_BUTTON_PADDING;
    private boolean enabled = true;
    private UiCallback action;
    private boolean wasPressed;
    private int keyCode;
    private boolean isKeyPressed;
    private boolean isAreaPressed;
    private boolean isMouseOver;
    private boolean isSoundEnabled = false;

    public UiActionButton setSoundEnabled(boolean soundEnabled) {
        isSoundEnabled = soundEnabled;
        return this;
    }

    public UiActionButton setKeyCode(int keyCode) {
        this.keyCode = keyCode;
        return this;
    }

    public UiActionButton setAction(UiCallback action) {
        this.action = action;
        return this;
    }

    public UiActionButton setPadding(int padding) {
        this.padding = padding;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public UiActionButton setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public UiActionButton setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        containedElement.setPosition(x, y);
        return this;
    }

    @Override
    public UiActionButton setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
        return this;
    }

    @Override
    public void draw() {
        Color tint = SolColor.UI_INACTIVE;
        if (isEnabled()) {
            tint = SolColor.UI_DARK;
            if (isMouseOver) {
                tint = SolColor.UI_MED;
            }
            if (wasPressed || isKeyPressed || isAreaPressed) {
                tint = SolColor.UI_LIGHT;
            }
        }
        SolApplication.getUiDrawer().draw(getScreenArea(), tint);
        if (containedElement != null) {
            containedElement.draw();
        }
    }

    @Override
    public boolean maybeFlashPressed(int keyCode) {
        boolean isPressed = isEnabled() && keyCode == this.keyCode;
        if (containedElement != null) {
            isPressed = isPressed || containedElement.maybeFlashPressed(keyCode);
        }
        return isPressed;
    }

    @Override
    public boolean maybeFlashPressed(SolInputManager.InputPointer inputPointer) {
        return isEnabled() && getScreenArea().contains(inputPointer.x, inputPointer.y);
    }

    @Override
    public boolean update(SolInputManager.InputPointer[] inputPointers, boolean cursorShown, boolean canBePressed, SolInputManager inputMan, SolApplication cmp) {
        boolean wasMouseOver = isMouseOver;
        isMouseOver = Stream.of(inputPointers).anyMatch(this::maybeFlashPressed);
        if (isSoundEnabled && isMouseOver && !wasMouseOver) {
            inputMan.playHover(cmp);
        }
        isAreaPressed = Stream.of(inputPointers).anyMatch(inputPointer -> inputPointer.pressed && maybeFlashPressed(inputPointer));
        isKeyPressed = isEnabled() && Gdx.input.isKeyPressed(keyCode);

        boolean isPressed = isKeyPressed || isAreaPressed;
        if (wasPressed && !isPressed) {
            inputMan.playClick(cmp);
            if (action != null) {
                action.callback(this);
            }
        }
        wasPressed = isPressed;
        return isPressed;
    }

    @Override
    public void blur() {
        isAreaPressed = false;
        isKeyPressed = false;
        wasPressed = false;
    }

    @Override
    public UiActionButton setWidth(int width) {
        if (containedElement instanceof UiResizableElement) {
            if (((UiResizableElement) containedElement).getMinWidth() + 2 * padding <= width) {
                ((UiResizableElement) containedElement).setWidth(width - 2 * padding);
            } else {
                ((UiResizableElement) containedElement).setWidth(((UiResizableElement) containedElement).getMinWidth());
            }
        }
        this.width = width;
        return this;
    }

    @Override
    public UiActionButton setHeight(int height) {
        if (containedElement instanceof UiResizableElement) {
            if (((UiResizableElement) containedElement).getMinHeight() + 2 * padding <= height) {
                ((UiResizableElement) containedElement).setHeight(height - 2 * padding);
            } else {
                ((UiResizableElement) containedElement).setHeight(((UiResizableElement) containedElement).getMinHeight());
            }
        }
        this.height = height;
        return this;
    }

    @Override
    public int getDefaultHeight() {
        if (containedElement instanceof UiResizableElement) {
            return Math.max(((UiResizableElement) containedElement).getDefaultHeight() + 2 * padding, DEFAULT_BUTTON_HEIGHT);
        }
        return Math.max(getMinHeight() + padding * 2, DEFAULT_BUTTON_HEIGHT);
    }

    @Override
    public int getDefaultWidth() {
        if (containedElement instanceof UiResizableElement) {
            return Math.max(((UiResizableElement) containedElement).getDefaultWidth() + 2 * padding, DEFAULT_BUTTON_WIDTH);
        }
        return Math.max(getMinWidth() + padding * 2, DEFAULT_BUTTON_WIDTH);
    }

    @Override
    public int getMinHeight() {
        if (containedElement == null) {
            return 0;
        }
        if (containedElement instanceof UiResizableElement) {
            return ((UiResizableElement) containedElement).getMinHeight();
        }
        return containedElement.getHeight();
    }

    @Override
    public int getMinWidth() {
        if (containedElement == null) {
            return 0;
        }
        if (containedElement instanceof UiResizableElement) {
            return ((UiResizableElement) containedElement).getMinWidth();
        }
        return containedElement.getWidth();
    }

    @Override
    public UiActionButton addElement(UiElement element) {
        containedElement = element;
        recalculate();
        return this;
    }

    @Override
    public UiActionButton recalculate() {
        setHeight(getDefaultHeight());
        setWidth(getDefaultWidth());
        return this;
    }
}
