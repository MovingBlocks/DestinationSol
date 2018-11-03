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
import org.destinationsol.common.SolColor;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.ResizeSubscriber;
import org.destinationsol.ui.SolInputManager;

import java.util.Optional;

//TODO unfinished. Idea behind this class: fits contents + padding, by default on screen center, can be dragged around by dragging the padding. Meant to be root element.
public class UiWindow extends AbstractUiElement implements ResizeSubscriber {

    private int x;
    private int y;
    private boolean isManuallyMoved = false;
    private int width;
    private int height;
    private Rectangle screenArea;
    private Rectangle innerArea;

    public UiWindow() {
        SolApplication.addResizeSubscriber(this);
        resize();
    }

    @Override
    public UiWindow setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public UiWindow setParent(UiContainerElement parent) {
        this.parent = Optional.of(parent);
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
    public void draw() {
        SolApplication.getUiDrawer().draw(screenArea, SolColor.UI_BG_LIGHT);
        SolApplication.getUiDrawer().draw(innerArea, SolColor.UI_BG_LIGHT);
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
        return screenArea;
    }

    @Override
    public void blur() {
        isManuallyMoved = false;
    }

    @Override
    public void resize() {
        final DisplayDimensions displayDimensions = SolApplication.displayDimensions;
        if (!isManuallyMoved) {
            setPosition(displayDimensions.getWidth() / 2, displayDimensions.getHeight() / 2);
        }
        width = displayDimensions.getWidth() / 2;
        height = displayDimensions.getHeight() / 2;
        calculateScreenArea();
    }

    private void calculateScreenArea() {
        DisplayDimensions displayDimensions = SolApplication.displayDimensions;
        screenArea = new Rectangle((x - width/2) * displayDimensions.getRatio() / displayDimensions.getWidth(), (y - height/2) / (float)displayDimensions.getHeight(), width * displayDimensions.getRatio() / displayDimensions.getWidth(), height / (float)displayDimensions.getHeight());
        innerArea = new Rectangle(screenArea.x + 0.02f, screenArea.y + 0.02f, screenArea.width - 0.04f, screenArea.height - 0.04f);
    }
}
