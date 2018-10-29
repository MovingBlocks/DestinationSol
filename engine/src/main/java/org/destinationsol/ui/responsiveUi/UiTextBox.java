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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UiTextBox extends AbstractUiElement {
    private int x;
    private int y;
    private List<String> lines = new ArrayList<>();

    @Override
    public UiTextBox setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public UiTextBox setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
        return this;
    }

    @Override
    public UiTextBox recalculate() {

        return this;
    }

    /**
     * Sets the contents of this element to the given {@code text}.
     *
     * Supports newlines through {@code '\n'} character. No other special characters are supported.
     * @param text Text to set
     * @return Self for method chaining
     */
    public UiTextBox setText(String text) {
        lines.clear();
        lines.addAll(Arrays.asList(text.split("\n")));
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
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void draw() {

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
        return false;
    }

    @Override
    public Rectangle getScreenArea() {
        return null;
    }

    @Override
    public void blur() {

    }
}
