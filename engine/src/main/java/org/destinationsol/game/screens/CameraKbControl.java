/*
 * Copyright 2017 MovingBlocks
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
import org.destinationsol.ui.SolUiControl;

import java.util.List;

class CameraKbControl {

    private static SolUiControl controlUp;
    private static SolUiControl controlDown;
    private static SolUiControl controlLeft;
    private static SolUiControl controlRight;

    CameraKbControl(GameOptions gameOptions, List<SolUiControl> controls) {
        controlUp = new SolUiControl(null, false, gameOptions.getKeyUp()); // Dirty solution, but on mixed controls, it is uncontrollable
        controls.add(controlUp);
        controlDown = new SolUiControl(null, false, gameOptions.getKeyDown());
        controls.add(controlDown);
        controlLeft = new SolUiControl(null, false, gameOptions.getKeyLeft());
        controls.add(controlLeft);
        controlRight = new SolUiControl(null, false, gameOptions.getKeyRight());
        controls.add(controlRight);
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
