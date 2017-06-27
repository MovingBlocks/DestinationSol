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
package org.destinationsol.game.screens;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// XBOX 360 Button Mapping
// 0 = Up
// 1 = Down
// 2 = Left
// 3 = Right
// 4 = Start
// 5 = Back
// 6 = Left Stick
// 7 = Right Stick
// 8 = LB
// 9 = RB
// 10 = Middle
// 11 = A
// 12 = B
// 13 = X
// 14 = Y

// XBOX 360 Axis Mapping
// 0 = LT
// 1 = RT
// 2 = Left Horizontal
// 3 = Left Vertical
// 4 = Right Horizontal
// 5 = Right Vertical

public class ShipControllerControl implements ShipUiControl {
    private static Logger logger = LoggerFactory.getLogger(ShipControllerControl.class);

    private boolean controllerShoot;
    private boolean controllerShoot2;
    private boolean controllerAbility;
    private boolean controllerLeft;
    private boolean controllerRight;
    private boolean controllerUp;
    private boolean controllerDown;

    ShipControllerControl(SolApplication solApplication) {
        final GameOptions gameOptions = solApplication.getOptions();

        Controllers.clearListeners();

        // print the currently connected controllers to the console
        logger.debug("Controllers Size: {}", Controllers.getControllers().size);
        int i = 0;
        for (Controller controller : Controllers.getControllers()) {
            logger.debug("#{}:{}", i++, controller.getName());
        }

        // setup the listener that prints events to the console
        Controllers.addListener(new ControllerListener() {
            int indexOf(Controller controller) {
                return Controllers.getControllers().indexOf(controller, true);
            }

            @Override
            public void connected(Controller controller) {
            }

            @Override
            public void disconnected(Controller controller) {
            }

            @Override
            public boolean buttonDown(Controller controller, int buttonIndex) {
                if (buttonIndex == gameOptions.getControllerButtonShoot()) {
                    controllerShoot = true;
                } else if (buttonIndex == gameOptions.getControllerButtonShoot2()) {
                    controllerShoot2 = true;
                } else if (buttonIndex == gameOptions.getControllerButtonAbility()) {
                    controllerAbility = true;
                } else if (buttonIndex == gameOptions.getControllerButtonLeft()) {
                    controllerLeft = true;
                } else if (buttonIndex == gameOptions.getControllerButtonRight()) {
                    controllerRight = true;
                } else if (buttonIndex == gameOptions.getControllerButtonUp()) {
                    controllerUp = true;
                }

                return true;
            }

            @Override
            public boolean buttonUp(Controller controller, int buttonIndex) {
                if (buttonIndex == gameOptions.getControllerButtonShoot()) {
                    controllerShoot = false;
                } else if (buttonIndex == gameOptions.getControllerButtonShoot2()) {
                    controllerShoot2 = false;
                } else if (buttonIndex == gameOptions.getControllerButtonAbility()) {
                    controllerAbility = false;
                } else if (buttonIndex == gameOptions.getControllerButtonLeft()) {
                    controllerLeft = false;
                } else if (buttonIndex == gameOptions.getControllerButtonRight()) {
                    controllerRight = false;
                } else if (buttonIndex == gameOptions.getControllerButtonUp()) {
                    controllerUp = false;
                }

                return true;
            }

            @Override
            public boolean axisMoved(Controller controller, int axisIndex, float value) {
                if (axisIndex == gameOptions.getControllerAxisShoot()) {
                    controllerShoot = (value > 0.5f);
                } else if (axisIndex == gameOptions.getControllerAxisShoot2()) {
                    controllerShoot2 = (value > 0.5f);
                } else if (axisIndex == gameOptions.getControllerAxisAbility()) {
                    controllerAbility = (value > 0.5f);
                } else if (axisIndex == gameOptions.getControllerAxisLeftRight()) {
                    boolean invert = gameOptions.isControllerAxisLeftRightInverted();
                    if (value < -0.5f) {
                        controllerLeft = !invert;
                        controllerRight = invert;
                    } else if (value > 0.5f) {
                        controllerLeft = invert;
                        controllerRight = !invert;
                    } else {
                        controllerLeft = false;
                        controllerRight = false;
                    }
                } else if (axisIndex == gameOptions.getControllerAxisUpDown()) {
                    boolean invert = gameOptions.isControllerAxisUpDownInverted();
                    if (value < -0.5f) {
                        controllerUp = !invert;
                        controllerDown = invert;
                    } else if (value > 0.5f) {
                        controllerUp = invert;
                        controllerDown = !invert;
                    } else {
                        controllerUp = false;
                        controllerDown = false;
                    }
                }

                return true;
            }

            @Override
            public boolean povMoved(Controller controller, int povIndex, PovDirection value) {
                logger.debug("#{}, pov {}: {}", indexOf(controller), povIndex, value);
                return false;
            }

            @Override
            public boolean xSliderMoved(Controller controller, int sliderIndex, boolean value) {
                logger.debug("#{},  x slider  {}: {}", indexOf(controller), sliderIndex, value);
                return false;
            }

            @Override
            public boolean ySliderMoved(Controller controller, int sliderIndex, boolean value) {
                logger.debug("#{},  y slider  {}: {}", indexOf(controller), sliderIndex, value);
                return false;
            }

            @Override
            public boolean accelerometerMoved(Controller controller, int accelerometerIndex, Vector3 value) {
                // not printing this as we get too many values
                return false;
            }
        });

    }

    @Override
    public boolean isLeft() {
        return controllerLeft;
    }

    @Override
    public boolean isRight() {
        return controllerRight;
    }

    @Override
    public boolean isUp() {
        return controllerUp;
    }

    @Override
    public boolean isDown() {
        return controllerDown;
    }

    @Override
    public boolean isShoot() {
        return controllerShoot;
    }

    @Override
    public boolean isShoot2() {
        return controllerShoot2;
    }

    @Override
    public boolean isAbility() {
        return controllerAbility;
    }
}
