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

import org.destinationsol.SolApplication;

import java.util.List;

public interface SolUiScreen {
    List<SolUiControl> getControls();

    default void onAdd(SolApplication solApplication) {
        // Intentionally left blank
    }

    default void updateCustom(SolApplication solApplication, SolInputManager.InputPointer[] inputPointers, boolean clickedOutside) {
        // Intentionally left blank
    }

    default boolean isCursorOnBg(SolInputManager.InputPointer inputPointer) {
        return false;
    }

    default void blurCustom(SolApplication solApplication) {
        // Intentionally left blank
    }

    default void drawBg(UiDrawer uiDrawer, SolApplication solApplication) {
        // Intentionally left blank
    }

    default void drawImgs(UiDrawer uiDrawer, SolApplication solApplication) {
        // Intentionally left blank
    }

    default void drawText(UiDrawer uiDrawer, SolApplication solApplication) {
        // Intentionally left blank
    }

    default boolean reactsToClickOutside() {
        return false;
    }
}
