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

public interface UiElement {
    // Set position. Returns UiElement to support Builder Pattern.
    UiElement setPosition(int x, int y);

    // Set dimensions. Returns UiElement to support Builder Pattern.
    UiElement setDimensions(int width, int height);

    int getX();

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
    void blur();
}
