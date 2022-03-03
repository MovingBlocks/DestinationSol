/*
 * Copyright 2022 The Terasology Foundation
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

package org.destinationsol.input;

import com.badlogic.gdx.Input;
import org.destinationsol.GameOptions;

import java.util.EnumSet;

/**
 * These are the default controls used in the base game.
 * You can obtain the inputs used to trigger these controls using {@link GameOptions#getControl(InputControls)}.
 */
public enum DefaultControls implements InputControls {
    MOUSE_UP("keyUpMouse", "Up", EnumSet.of(GameOptions.ControlType.MIXED), Input.Keys.W),
    MOUSE_DOWN("keyDownMouse", "Down", EnumSet.of(GameOptions.ControlType.MIXED), Input.Keys.S),
    UP("keyUp", "Up", EnumSet.of(GameOptions.ControlType.KEYBOARD), Input.Keys.UP),
    DOWN("keyDown", "Down", EnumSet.of(GameOptions.ControlType.KEYBOARD), Input.Keys.DOWN),
    LEFT("keyLeft", "Left", EnumSet.of(GameOptions.ControlType.KEYBOARD), Input.Keys.LEFT),
    RIGHT("keyRight", "Right", EnumSet.of(GameOptions.ControlType.KEYBOARD), Input.Keys.RIGHT),
    SHOOT("keyShoot", "Shoot", EnumSet.of(GameOptions.ControlType.KEYBOARD), Input.Keys.SPACE),
    SHOOT2("keyShoot2", "Shoot Secondary", EnumSet.of(GameOptions.ControlType.KEYBOARD), Input.Keys.CONTROL_LEFT),
    ABILITY("keyAbility", "Ability", EnumSet.of(GameOptions.ControlType.KEYBOARD), Input.Keys.SHIFT_LEFT),
    ESCAPE("keyEscape", "Escape", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.ESCAPE),
    MAP("keyMap", "Map", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.TAB),
    INVENTORY("keyInventory", "Inventory", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.I),
    TALK("keyTalk", "Talk", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.T),
    PAUSE("keyPause", "Pause", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.P),
    DROP("keyDrop", "Drop", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.D),
    SELL("keySellMenu", "Sell", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.S),
    BUY("keyBuyMenu", "Buy", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.B),
    CHANGE_SHIP("keyChangeShipMenu", "Change Ship", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.C),
    HIRE_SHIP("keyHireShipMenu", "Hire Ship", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.H),
    MERCENARY_INTERACTION("keyMercenaryInteraction", "Hire Ship", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.M),
    FREE_CAMERA_MOVEMENT("keyFreeCameraMovement", "Free Camera", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.V),
    ZOOM_IN("keyZoomIn", "Zoom In", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.PAGE_UP),
    ZOOM_OUT("keyZoomOut", "Zoom Out", EnumSet.allOf(GameOptions.ControlType.class), Input.Keys.PAGE_DOWN);

    private final String controlName;
    private final String displayName;
    private final EnumSet<GameOptions.ControlType> controlTypes;
    private final int[] defaultInputs;

    DefaultControls(String controlName, String displayName, EnumSet<GameOptions.ControlType> controlTypes, int... defaultInputs) {
        this.controlName = controlName;
        this.displayName = displayName;
        this.controlTypes = controlTypes;
        this.defaultInputs = defaultInputs;
    }

    public String getControlName() {
        return controlName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public EnumSet<GameOptions.ControlType> getControlTypes() {
        return controlTypes;
    }

    public int[] getDefaultInputs() {
        return defaultInputs;
    }
}