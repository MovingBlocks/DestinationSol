package com.miloshpetrov.sol2.game.screens;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.miloshpetrov.sol2.GameOptions;
import com.miloshpetrov.sol2.SolApplication;

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

public class ShipControllerControl implements ShipUiControl  {
    private boolean controllerShoot;
    private boolean controllerShoot2;
    private boolean controllerAbility;
    private boolean controllerLeft;
    private boolean controllerRight;
    private boolean controllerUp;
    private boolean controllerDown;


    public ShipControllerControl(SolApplication cmp) {
        final GameOptions gameOptions = cmp.getOptions();

        Controllers.clearListeners();

        // print the currently connected controllers to the console
        System.out.println("Controllers: " + Controllers.getControllers().size);
        int i = 0;
        for (Controller controller : Controllers.getControllers()) {
            System.out.println("#" + i++ + ": " + controller.getName());
        }

        // setup the listener that prints events to the console
        Controllers.addListener(new ControllerListener() {
            public int indexOf (Controller controller) {
                return Controllers.getControllers().indexOf(controller, true);
            }

            @Override
            public void connected (Controller controller) {
            }

            @Override
            public void disconnected (Controller controller) {
            }

            @Override
            public boolean buttonDown (Controller controller, int buttonIndex) {
                if (buttonIndex == gameOptions.getControllerButtonShoot()){
                    controllerShoot = true;
                } else if (buttonIndex == gameOptions.getControllerButtonShoot2()){
                    controllerShoot2 = true;
                } else if (buttonIndex == gameOptions.getControllerButtonAbility()){
                    controllerAbility = true;
                } else if (buttonIndex == gameOptions.getControllerButtonLeft()){
                    controllerLeft = true;
                } else if (buttonIndex == gameOptions.getControllerButtonRight()){
                    controllerRight = true;
                } else if (buttonIndex == gameOptions.getControllerButtonUp()){
                    controllerUp = true;
                }

                return true;
            }

            @Override
            public boolean buttonUp (Controller controller, int buttonIndex) {
                if (buttonIndex == gameOptions.getControllerButtonShoot()){
                    controllerShoot = false;
                } else if (buttonIndex == gameOptions.getControllerButtonShoot2()){
                    controllerShoot2 = false;
                } else if (buttonIndex == gameOptions.getControllerButtonAbility()){
                    controllerAbility = false;
                } else if (buttonIndex == gameOptions.getControllerButtonLeft()){
                    controllerLeft = false;
                } else if (buttonIndex == gameOptions.getControllerButtonRight()){
                    controllerRight = false;
                } else if (buttonIndex == gameOptions.getControllerButtonUp()){
                    controllerUp = false;
                }

                return true;
            }

            @Override
            public boolean axisMoved (Controller controller, int axisIndex, float value) {
                if (axisIndex == gameOptions.getControllerAxisShoot()){
                    if (value > 0.5f) {
                        controllerShoot = true;
                    } else {
                        controllerShoot = false;
                    }
                }

                else if (axisIndex == gameOptions.getControllerAxisShoot2()){
                    if (value > 0.5f) {
                        controllerShoot2 = true;
                    } else {
                        controllerShoot2 = false;
                    }
                }

                else if (axisIndex == gameOptions.getControllerAxisAbility()){
                    if (value > 0.5f) {
                        controllerAbility = true;
                    } else {
                        controllerAbility = false;
                    }
                }

                else if (axisIndex == gameOptions.getControllerAxisLeftRight()){
                    boolean invert = gameOptions.isControllerAxisLeftRightInverted();
                    if (value < -0.5f) {
                        controllerLeft = invert ? false : true;
                        controllerRight = invert ? true : false;
                    } else if (value > 0.5f) {
                        controllerLeft = invert ? true : false;
                        controllerRight = invert ? false : true;
                    } else {
                        controllerLeft = false;
                        controllerRight = false;
                    }
                }

                else if (axisIndex == gameOptions.getControllerAxisUpDown()){
                    boolean invert = gameOptions.isControllerAxisUpDownInverted();
                    if (value < -0.5f) {
                        controllerUp = invert ? false : true;
                        controllerDown = invert ? true : false;
                    } else if (value > 0.5f) {
                        controllerUp = invert ? true : false;
                        controllerDown = invert ? false : true;
                    } else {
                        controllerUp = false;
                        controllerDown = false;
                    }
                }

                return true;
            }

            @Override
            public boolean povMoved (Controller controller, int povIndex, PovDirection value) {
                System.out.println("#" + indexOf(controller) + ", pov " + povIndex + ": " + value);
                return false;
            }

            @Override
            public boolean xSliderMoved (Controller controller, int sliderIndex, boolean value) {
                System.out.println("#" + indexOf(controller) + ", x slider " + sliderIndex + ": " + value);
                return false;
            }

            @Override
            public boolean ySliderMoved (Controller controller, int sliderIndex, boolean value) {
                System.out.println("#" + indexOf(controller) + ", y slider " + sliderIndex + ": " + value);
                return false;
            }

            @Override
            public boolean accelerometerMoved (Controller controller, int accelerometerIndex, Vector3 value) {
                // not printing this as we get to many values
                return false;
            }
        });

    }

    @Override
    public void update(SolApplication cmp, boolean enabled) {
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

    @Override
    public TextureAtlas.AtlasRegion getInGameTex() {
        return null;
    }

    @Override
    public void blur() {

    }
}
