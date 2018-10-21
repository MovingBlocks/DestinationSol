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
package org.destinationsol.game.screens;

import org.destinationsol.GameOptions;
import org.destinationsol.ui.responsiveUi.UiHeadlessButton;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;

class CameraKeyboardControl {
    private static UiHeadlessButton controlUp;
    private static UiHeadlessButton controlDown;
    private static UiHeadlessButton controlLeft;
    private static UiHeadlessButton controlRight;

    CameraKeyboardControl(GameOptions gameOptions, UiRelativeLayout rootUiElement) {
        controlUp = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyUp()); // Dirty solution, but on mixed controls, it is uncontrollable
        rootUiElement.addHeadlessElement(controlUp);

        controlDown = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyDown());
        rootUiElement.addHeadlessElement(controlDown);

        controlLeft = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyLeft());
        rootUiElement.addHeadlessElement(controlLeft);

        controlRight = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyRight());
        rootUiElement.addHeadlessElement(controlRight);
    }

    boolean isDown() {
        return controlDown.isOn();
    }

    boolean isUp() {
        return controlUp.isOn();
    }

    boolean isLeft() {
        return controlLeft.isOn();
    }

    boolean isRight() {
        return controlRight.isOn();
    }
}
