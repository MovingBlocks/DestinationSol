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
package org.destinationsol.ui;

import org.destinationsol.SolApplication;
import org.destinationsol.ui.responsiveUi.UiElement;
import org.destinationsol.ui.responsiveUi.UiNoneElement;

public abstract class SolUiBaseScreen implements SolUiScreen, ResizeSubscriber {
    protected UiElement rootUiElement = new UiNoneElement();

    protected SolUiBaseScreen() {
        SolApplication.addResizeSubscriber(this);
    }

    @Override
    public UiElement getRootUiElement() {
        return rootUiElement;
    }

    @Override
    public void onAdd(SolApplication solApplication) {
        // Intentionally left blank
    }

    @Override
    public void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        // Intentionally left blank
    }

    @Override
    public boolean isCursorOnBackground(SolInputManager.InputPointer inputPointer) {
        return false;
    }

    @Override
    public void blurCustom(SolApplication solApplication) {
        // Intentionally left blank
    }

    @Override
    public void drawBackground(UiDrawer uiDrawer, SolApplication solApplication) {
        // Intentionally left blank
    }

    @Override
    public void draw(UiDrawer uiDrawer, SolApplication solApplication) {
        // Intentionally left blank
    }

    @Override
    public boolean reactsToClickOutside() {
        return false;
    }

    @Override
    public void resize() {
        // TODO: Remove this check.
        if (rootUiElement != null) {
            rootUiElement.setPosition(0, 0);
        }
    }
}
