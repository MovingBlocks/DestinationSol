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
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.SolInputManager;

import java.util.Optional;

public abstract class AbstractUiElement implements UiElement {
    protected Optional<UiContainerElement> parent = Optional.empty();
    protected int x = 0;
    protected int y = 0;
    protected int height = 0;
    protected int width = 0;

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public Optional<UiContainerElement> getParent() {
        return parent;
    }

    @Override
    public Rectangle getScreenArea() {
        DisplayDimensions displayDimensions = SolApplication.displayDimensions;
        return new Rectangle(
                displayDimensions.getFloatWidthForPixelWidth(getX() - getWidth()/2),
                displayDimensions.getFloatHeightForPixelHeight(getY() - getHeight()/2),
                displayDimensions.getFloatWidthForPixelWidth(getWidth()),
                displayDimensions.getFloatHeightForPixelHeight(getHeight())
        );
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
    public void blur() {

    }
}
