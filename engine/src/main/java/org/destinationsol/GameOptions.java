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
package org.destinationsol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.google.common.base.Enums;
import org.destinationsol.input.DefaultControls;
import org.destinationsol.input.InputControls;
import org.destinationsol.menu.Resolution;
import org.destinationsol.menu.ResolutionProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

public class GameOptions {
    public enum ControlType {
        KEYBOARD("Keyboard"),
        MIXED("KB + Mouse"),
        MOUSE("Mouse"),
        CONTROLLER("Controller");

        private String humanName; // String used in the options menu.

        ControlType(String humanName) {
            this.humanName = humanName;
        }

        public String getHumanName() {
            return this.humanName;
        }

        public ControlType nextType(boolean isMobile) {
            switch (this) {
                case MIXED:
                    return CONTROLLER;
                case CONTROLLER:
                case MOUSE:
                    return KEYBOARD;
                case KEYBOARD:
                default:
                    return isMobile ? MOUSE : MIXED;
            }
        }
    }

    public enum Volume {
        OFF("Off", 0f),
        LOW("Low", 0.25f),
        MEDIUM("Medium", 0.5f),
        HIGH("High", 0.75f),
        MAX("Max", 1f);

        private final String name;

        private final float volume;

        Volume(String name, float volume) {
            this.name = name;
            this.volume = volume;
        }

        public String getName() {
            return name;
        }

        public float getVolume() {
            return volume;
        }

        public Volume advance() {
            switch (this) {
                case OFF:
                    return LOW;
                case LOW:
                    return MEDIUM;
                case MEDIUM:
                    return HIGH;
                case HIGH:
                    return MAX;
                case MAX:
                    return OFF;
            }
            return MAX;
        }
    }

    public static final String FILE_NAME = "settings.ini";
    public static final int DEFAULT_AXIS_SHOOT = 1;
    public static final int DEFAULT_AXIS_SHOOT2 = 0;
    public static final int DEFAULT_AXIS_ABILITY = -1;
    public static final int DEFAULT_AXIS_LEFT_RIGHT = 2;
    public static final boolean DEFAULT_AXIS_LEFT_RIGHT_INVERTED = false;
    public static final int DEFAULT_AXIS_UP_DOWN = 5;
    public static final boolean DEFAULT_AXIS_UP_DOWN_INVERTED = false;
    public static final int DEFAULT_BUTTON_SHOOT = -1;
    public static final int DEFAULT_BUTTON_SHOOT2 = -1;
    public static final int DEFAULT_BUTTON_ABILITY = 14;
    public static final int DEFAULT_BUTTON_UP = -1;
    public static final int DEFAULT_BUTTON_DOWN = -1;
    public static final int DEFAULT_BUTTON_LEFT = -1;
    public static final int DEFAULT_BUTTON_RIGHT = -1;
    public static final int DEFAULT_MAP_SCROLL_SPEED = 10;
    public static final int DEFAULT_MOBILE_MAP_SCROLL_SPEED = 5;
    private static final float DEFAULT_NUI_UI_SCALE = 1.0f;

    public int x;
    public int y;
    public boolean fullscreen;
    public ControlType controlType;
    public Volume sfxVolume;
    public Volume musicVolume;
    public boolean canSellEquippedItems;
    private int controllerAxisShoot;
    private int controllerAxisShoot2;
    private int controllerAxisAbility;
    private int controllerAxisLeftRight;
    private boolean isControllerAxisLeftRightInverted;
    private int controllerAxisUpDown;
    private boolean isControllerAxisUpDownInverted;
    private int controllerButtonShoot;
    private int controllerButtonShoot2;
    private int controllerButtonAbility;
    private int controllerButtonLeft;
    private int controllerButtonRight;
    private int controllerButtonUp;
    private int controllerButtonDown;
    private int mapScrollSpeed;
    public float nuiUiScale;
    private Map<InputControls, int[]> controls;

    private ResolutionProvider resolutionProvider;

    public GameOptions(boolean mobile, SolFileReader solFileReader) {
        controls = new HashMap<>();

        IniReader reader = new IniReader(FILE_NAME, solFileReader);
        for (DefaultControls control : DefaultControls.values()) {
            String controlValue = reader.getString(control.getControlName(), "");
            if (controlValue.isEmpty()) {
                controls.put(control, control.getDefaultInputs());
            } else {
                controls.put(control, parseKeyboardControl(controlValue));
            }
        }

        x = reader.getInt("x", 1366);
        y = reader.getInt("y", 768);
        fullscreen = reader.getBoolean("fullscreen", false);
        controlType = mobile ? ControlType.KEYBOARD : Enums.getIfPresent(ControlType.class,  reader.getString("controlType", "MIXED")).or(ControlType.MIXED);
        sfxVolume = Enums.getIfPresent(Volume.class, reader.getString("sfxVolume", "MAX")).or(Volume.MAX);
        musicVolume = Enums.getIfPresent(Volume.class, reader.getString("musicVolume", "MAX")).or(Volume.MAX);
        controllerAxisShoot = reader.getInt("controllerAxisShoot", DEFAULT_AXIS_SHOOT);
        controllerAxisShoot2 = reader.getInt("controllerAxisShoot2", DEFAULT_AXIS_SHOOT2);
        controllerAxisAbility = reader.getInt("controllerAxisAbility", DEFAULT_AXIS_ABILITY);
        controllerAxisLeftRight = reader.getInt("controllerAxisLeftRight", DEFAULT_AXIS_LEFT_RIGHT);
        isControllerAxisLeftRightInverted = reader.getBoolean("isControllerAxisLeftRightInverted", DEFAULT_AXIS_LEFT_RIGHT_INVERTED);
        controllerAxisUpDown = reader.getInt("controllerAxisUpDown", DEFAULT_AXIS_UP_DOWN);
        isControllerAxisUpDownInverted = reader.getBoolean("isControllerAxisUpDownInverted", DEFAULT_AXIS_UP_DOWN_INVERTED);
        controllerButtonShoot = reader.getInt("controllerButtonShoot", DEFAULT_BUTTON_SHOOT);
        controllerButtonShoot2 = reader.getInt("controllerButtonShoot2", DEFAULT_BUTTON_SHOOT2);
        controllerButtonAbility = reader.getInt("controllerButtonAbility", DEFAULT_BUTTON_ABILITY);
        controllerButtonLeft = reader.getInt("controllerButtonLeft", DEFAULT_BUTTON_LEFT);
        controllerButtonRight = reader.getInt("controllerButtonRight", DEFAULT_BUTTON_RIGHT);
        controllerButtonUp = reader.getInt("controllerButtonUp", DEFAULT_BUTTON_UP);
        controllerButtonDown = reader.getInt("controllerButtonDown", DEFAULT_BUTTON_DOWN);
        canSellEquippedItems = reader.getBoolean("canSellEquippedItems", false);
        mapScrollSpeed = reader.getInt("mapScrollSpeed", mobile ? DEFAULT_MOBILE_MAP_SCROLL_SPEED : DEFAULT_MAP_SCROLL_SPEED);
        nuiUiScale = reader.getFloat("nuiUiScale", DEFAULT_NUI_UI_SCALE);
    }

    private int[] parseKeyboardControl(String controlString) {
        String[] inputNames = controlString.split(",");
        int[] inputKeys = new int[inputNames.length];
        for (int inputNo = 0; inputNo < inputNames.length; inputNo++) {
            inputKeys[inputNo] = Input.Keys.valueOf(inputNames[inputNo]);
        }
        return inputKeys;
    }

    public void advanceResolution() {
        //lazy initialize provider because graphics is not available at the constructor
        if (resolutionProvider == null) {
            resolutionProvider = new ResolutionProvider(asList(Gdx.graphics.getDisplayModes()));
        }
        Resolution nextResolution = resolutionProvider.increase();

        x = nextResolution.getWidth();
        y = nextResolution.getHeight();

        save();
    }

    public void advanceControlType(boolean mobile) {
        controlType = controlType.nextType(mobile);
        save();
    }

    public void advanceFullscreen() {
        fullscreen = !fullscreen;
        save();
    }

    public void advanceSoundVolMul() {
        sfxVolume = sfxVolume.advance();
        save();
    }

    public void advanceMusicVolMul() {
        musicVolume = musicVolume.advance();
        save();
    }

    public void advanceMapScrollSpeed() {
        mapScrollSpeed++;
        if (mapScrollSpeed > 15) {
            mapScrollSpeed = 1;
        }
        save();
    }

    public void advanceNuiUiScale() {
        nuiUiScale += 0.25f;
        if (nuiUiScale > 2.0f) {
            nuiUiScale = 0.25f;
        }
        save();
    }

    /**
     * Save the configuration settings to file.
     */
    public void save() {
        List<Object> iniValues = new ArrayList<>();
        Collections.addAll(iniValues, "x", x, "y", y, "fullscreen", fullscreen, "controlType", controlType,
                "sfxVolume", sfxVolume, "musicVolume", musicVolume, "canSellEquippedItems", canSellEquippedItems,
                "controllerAxisShoot", getControllerAxisShoot(), "controllerAxisShoot2", getControllerAxisShoot2(),
                "controllerAxisAbility", getControllerAxisAbility(), "controllerAxisLeftRight", getControllerAxisLeftRight(),
                "isControllerAxisLeftRightInverted", isControllerAxisLeftRightInverted(), "controllerAxisUpDown", getControllerAxisUpDown(),
                "isControllerAxisUpDownInverted", isControllerAxisUpDownInverted(), "controllerButtonShoot", getControllerButtonShoot(),
                "controllerButtonShoot2", getControllerButtonShoot2(), "controllerButtonAbility", getControllerButtonAbility(),
                "controllerButtonLeft", getControllerButtonLeft(), "controllerButtonRight", getControllerButtonRight(),
                "controllerButtonUp", getControllerButtonUp(), "controllerButtonDown", getControllerButtonDown(),
                "mapScrollSpeed", getMapScrollSpeed(), "nuiUiScale", getNuiUiScale());

        StringBuilder inputsStringBuilder = new StringBuilder();
        for (Map.Entry<InputControls, int[]> control : controls.entrySet()) {
            iniValues.add(control.getKey().getControlName());

            int[] inputs = control.getValue();
            for (int inputNo = 0; inputNo < inputs.length - 1; inputNo++) {
                inputsStringBuilder.append(Input.Keys.toString(inputs[inputNo]));
                inputsStringBuilder.append(",");
            }
            inputsStringBuilder.append(Input.Keys.toString(inputs[inputs.length - 1]));
            iniValues.add(inputsStringBuilder.toString());

            inputsStringBuilder.delete(0, inputsStringBuilder.length());
        }
        IniReader.write(FILE_NAME, iniValues.toArray(new Object[0]));
    }

    /**
     * Get the defined key for up when the input controlType is set to CONTROL_MIXED. This includes navigating down in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyUpMouse() {
        return getControl(DefaultControls.MOUSE_UP)[0];
    }

    /**
     * Get the defined key for down when the input controlType is set to CONTROL_MIXED.
     * This includes activating the ship's thrusters and navigating up in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyDownMouse() {
        return getControl(DefaultControls.MOUSE_DOWN)[0];
    }

    /**
     * Get the readable name of the defined key for up when the input controlType is set to CONTROL_MIXED.
     * This includes navigating down in menus.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyUpMouseName() {
        return Input.Keys.toString(getKeyUpMouse());
    }

    public void setKeyUpMouseName(String keyUpMouseName) {
        setControl(DefaultControls.MOUSE_UP, new int[] { Input.Keys.valueOf(keyUpMouseName) });
    }

    /**
     * Get the readable name of the defined key for down when the input controlType is set to CONTROL_MIXED.
     * This includes navigating down in menus.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyDownMouseName() {
        return Input.Keys.toString(getKeyDownMouse());
    }

    public void setKeyDownMouseName(String keyDownMouseName) {
        setControl(DefaultControls.MOUSE_DOWN, new int[] { Input.Keys.valueOf(keyDownMouseName) });
    }

    /**
     * Get the defined key for up. This includes activating the ship's thrusters and navigating up in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyUp() {
        return getControl(DefaultControls.UP)[0];
    }

    /**
     * Get the readable name of the defined key for  up.
     * This includes activating the ship's thrusters and navigating up in menus.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyUpName() {
        return Input.Keys.toString(getKeyUp());
    }

    public void setKeyUpName(String keyUpName) {
        setControl(DefaultControls.UP, new int[] { Input.Keys.valueOf(keyUpName) });
    }

    /**
     * Get the defined key for down. This includes navigating down in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyDown() {
        return getControl(DefaultControls.DOWN)[0];
    }

    /**
     * Get the readable name of the defined key for down.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyDownName() {
        return Input.Keys.toString(getKeyDown());
    }

    public void setKeyDownName(String keyDownName) {
        setControl(DefaultControls.DOWN, new int[] { Input.Keys.valueOf(keyDownName) });
    }

    /**
     * Get the defined key for left. This includes rotating the ship left and navigating left in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyLeft() {
        return getControl(DefaultControls.LEFT)[0];
    }

    /**
     * Get the readable name of the defined key for left.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyLeftName() {
        return Input.Keys.toString(getKeyLeft());
    }

    public void setKeyLeftName(String keyLeftName) {
        setControl(DefaultControls.LEFT, new int[] { Input.Keys.valueOf(keyLeftName) });
    }

    /**
     * Get the defined key for right. This includes rotating the ship right and navigating right in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyRight() {
        return getControl(DefaultControls.RIGHT)[0];
    }

    /**
     * Get the readable name of the defined key for right.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyRightName() {
        return Input.Keys.toString(getKeyRight());
    }

    public void setKeyRightName(String keyRightName) {
        setControl(DefaultControls.RIGHT, new int[] { Input.Keys.valueOf(keyRightName) });
    }

    /**
     * Get the defined key for shooting the primary weapon.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyShoot() {
        return getControl(DefaultControls.SHOOT)[0];
    }

    /**
     * Get the readable name of the defined key for shooting the primary weapon.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyShootName() {
        return Input.Keys.toString(getKeyShoot());
    }

    public void setKeyShootName(String keyShootName) {
        setControl(DefaultControls.SHOOT, new int[] { Input.Keys.valueOf(keyShootName) });
    }

    /**
     * Get the defined key for shooting the secondary weapon.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyShoot2() {
        return getControl(DefaultControls.SHOOT2)[0];
    }

    /**
     * Get the readable name of the defined key for shooting the secondary weapon.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyShoot2Name() {
        return Input.Keys.toString(getKeyShoot2());
    }

    public void setKeyShoot2Name(String keyShoot2Name) {
        setControl(DefaultControls.SHOOT2, new int[] { Input.Keys.valueOf(keyShoot2Name) });
    }

    /**
     * Get the defined key for equipping and unequipping the primary weapon.
     * This is currently set to the same key as keyShootName
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyEquip() {
        return getKeyShoot();
    }

    /**
     * Get the defined key for equipping and unequipping the primary weapon.
     * This is currently set to the same key as keyShootName
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyEquipName() {
        return getKeyShootName();
    }

    /**
     * Get the defined key for equipping and unequipping the secondary weapon.
     * This is currently set to the same key as keyShoot2Name
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyEquip2() {
        return Input.Keys.valueOf(getKeyShoot2Name());
    }

    /**
     * Get the defined key for activating the ship's special ability.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyAbility() {
        return getControl(DefaultControls.ABILITY)[0];
    }

    /**
     * Get the readable name of the defined key for activating the ship's special ability.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyAbilityName() {
        return Input.Keys.toString(getKeyAbility());
    }

    public void setKeyAbilityName(String keyAbilityName) {
        setControl(DefaultControls.ABILITY, new int[] { Input.Keys.valueOf(keyAbilityName) });
    }

    /**
     * Get the defined key for escape. This includes bringing up the in-game menu and exiting in-game menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyEscape() {
        return getControl(DefaultControls.ESCAPE)[0];
    }

    /**
     * Get the readable name of the defined key for escape.
     * This includes bringing up the in-game menu and exiting in-game menus.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyEscapeName() {
        return Input.Keys.toString(getKeyEscape());
    }

    public void setKeyEscapeName(String keyEscapeName) {
        setControl(DefaultControls.ESCAPE, new int[] { Input.Keys.valueOf(keyEscapeName) });
    }

    /**
     * Get the defined key for activating the menu.
     * This is currently set to the same key as KeyEscape
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyMenu() {
        return getKeyEscape();
    }

    /**
     * Get the defined key for closing the menu.
     * This is currently set to the same key as KeyEscape
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyClose() {
        return getKeyEscape();
    }

    /**
     * Get the readable name of the defined key for closing the menu
     * This is currently set to the same key as KeyEscape
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyCloseName() {
        return getKeyEscapeName();
    }

    /**
     * Get the defined key for buying items.
     * This is currently set to the same key as KeyShoot
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyBuyItem() {
        return getKeyShoot();
    }

    /**
     * Get the defined key for buying items.
     * This is currently set to the same key as KeyShoot
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyBuyItemName() {
        return getKeyShootName();
    }

    /**
     * Get the defined key for selling items.
     * This is currently set to the same key as KeyShoot
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeySellItem() {
        return getKeyShoot();
    }

    /**
     * Get the defined key for hiring ships.
     * This is currently set to the same key as KeyShoot
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyHireShip() {
        return getKeyShoot();
    }

    /**
     * Get the defined key for changing ships.
     * This is currently set to the same key as KeyShoot
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyChangeShip() {
        return getKeyShoot();
    }

    /**
     * Get the defined key for zooming in on the map.
     * This is currently set to Page Up
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyZoomIn() {
        return getControl(DefaultControls.ZOOM_IN)[0];
    }

    /**
     * Get the readable name of the defined key for zooming in on the map.
     * This is currently set to Page Up
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyZoomInName() {
        return Input.Keys.toString(getKeyZoomIn());
    }

    /**
     * Get the defined key for zooming out on the map.
     * This is currently set to Page Down
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyZoomOut() {
        return getControl(DefaultControls.ZOOM_OUT)[0];
    }

    /**
     * Get the readable name of the defined key for zooming out on the map.
     * This is currently set to Page Down
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyZoomOutName() {
        return Input.Keys.toString(getKeyZoomOut());
    }

    /**
     * Get the defined key for opening and closing the map.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyMap() {
        return getControl(DefaultControls.MAP)[0];
    }

    /**
     * Get the readable name of the defined key for opening and closing the map.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyMapName() {
        return Input.Keys.toString(getKeyMap());
    }

    public void setKeyMapName(String keyMapName) {
        setControl(DefaultControls.MAP, new int[] { Input.Keys.valueOf(keyMapName) });
    }

    /**
     * Get the defined key for opening the inventory.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyInventory() {
        return getControl(DefaultControls.INVENTORY)[0];
    }

    /**
     * Get the readable name of the defined key for opening the inventory.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyInventoryName() {
        return Input.Keys.toString(getKeyInventory());
    }

    public void setKeyInventoryName(String keyInventoryName) {
        setControl(DefaultControls.INVENTORY, new int[] { Input.Keys.valueOf(keyInventoryName) });
    }

    /**
     * Get the defined key for interacting with mercenaries.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyMercenaryInteraction() {
        return getControl(DefaultControls.MERCENARY_INTERACTION)[0];
    }

    /**
     * Get the readable name of the defined key for interacting with mercenaries.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyMercenaryInterationName() {
        return Input.Keys.toString(getKeyMercenaryInteraction());
    }

    public void setKeyMercenaryInteractionName(String keyMercenaryInteractionName) {
        setControl(DefaultControls.MERCENARY_INTERACTION, new int[] { Input.Keys.valueOf(keyMercenaryInteractionName) });
    }

    public int getKeyFreeCameraMovement() {
        return getControl(DefaultControls.FREE_CAMERA_MOVEMENT)[0];
    }

    public String getKeyFreeCameraMovementName() {
        return Input.Keys.toString(getKeyFreeCameraMovement());
    }

    public void setKeyFreeCameraMovementName(String keyFreeCameraMovementName) {
        setControl(DefaultControls.FREE_CAMERA_MOVEMENT, new int[] { Input.Keys.valueOf(keyFreeCameraMovementName) });
    }

    /**
     * Get the defined key for opening the talk menu.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyTalk() {
        return getControl(DefaultControls.TALK)[0];
    }

    /**
     * Get the readable name of the defined key for opening the talk menu.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyTalkName() {
        return Input.Keys.toString(getKeyTalk());
    }

    public void setKeyTalkName(String keyTalkName) {
        setControl(DefaultControls.TALK, new int[] { Input.Keys.valueOf(keyTalkName) });
    }

    /**
     * Get the defined key for pausing and continuing the game.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyPause() {
        return getControl(DefaultControls.PAUSE)[0];
    }

    /**
     * Get the readable name of the defined key for pausing and continuing the game.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyPauseName() {
        return Input.Keys.toString(getKeyPause());
    }

    public void setKeyPauseName(String keyPauseName) {
        setControl(DefaultControls.PAUSE, new int[] { Input.Keys.valueOf(keyPauseName) });
    }

    /**
     * Get the defined key for dropping items.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyDrop() {
        return getControl(DefaultControls.DROP)[0];
    }

    /**
     * Get the readable name of the defined key for dropping items.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyDropName() {
        return Input.Keys.toString(getKeyDrop());
    }

    public void setKeyDropName(String keyDropName) {
        setControl(DefaultControls.DROP, new int[] { Input.Keys.valueOf(keyDropName) });
    }

    /**
     * Get the defined key for the sell menu.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeySellMenu() {
        return getControl(DefaultControls.SELL)[0];
    }

    /**
     * Get the readable name of the defined key for the sell menu.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeySellMenuName() {
        return Input.Keys.toString(getKeySellMenu());
    }

    public void setKeySellMenuName(String keySellMenuName) {
        setControl(DefaultControls.SELL, new int[] { Input.Keys.valueOf(keySellMenuName) });
    }

    /**
     * Get the defined key for the buy menu.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyBuyMenu() {
        return getControl(DefaultControls.BUY)[0];
    }

    /**
     * Get the readable name of the defined key for the buy menu.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyBuyMenuName() {
        return Input.Keys.toString(getKeyBuyMenu());
    }

    public void setKeyBuyMenuName(String keyBuyMenuName) {
        setControl(DefaultControls.BUY, new int[] { Input.Keys.valueOf(keyBuyMenuName) });
    }

    /**
     * Get the defined key for the change ship menu.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyChangeShipMenu() {
        return getControl(DefaultControls.CHANGE_SHIP)[0];
    }

    /**
     * Get the readable name of the defined key for the change ship menu.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyChangeShipMenuName() {
        return Input.Keys.toString(getKeyChangeShipMenu());
    }

    public void setKeyChangeShipMenuName(String keyChangeShipMenuName) {
        setControl(DefaultControls.CHANGE_SHIP, new int[] { Input.Keys.valueOf(keyChangeShipMenuName) });
    }

    /**
     * Get the defined key for the hire ship menu.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyHireShipMenu() {
        return getControl(DefaultControls.HIRE_SHIP)[0];
    }

    /**
     * Get the readable name of the defined key for the hire ship menu.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyHireShipMenuName() {
        return Input.Keys.toString(getKeyHireShipMenu());
    }

    public void setKeyHireShipMenuName(String keyHireShipMenuName) {
        setControl(DefaultControls.HIRE_SHIP, new int[] { Input.Keys.valueOf(keyHireShipMenuName) });
    }

    public Set<Map.Entry<InputControls, int[]>> getControls() {
        return controls.entrySet();
    }

    public int[] getControl(InputControls control) {
        return controls.get(control);
    }

    public void setControl(InputControls control, int[] keys) {
        controls.put(control, keys);
    }

    public int getControllerAxisShoot() {
        return controllerAxisShoot;
    }

    public void setControllerAxisShoot(int controllerAxisShoot) {
        this.controllerAxisShoot = controllerAxisShoot;
    }

    public int getControllerAxisShoot2() {
        return controllerAxisShoot2;
    }

    public void setControllerAxisShoot2(int controllerAxisShoot2) {
        this.controllerAxisShoot2 = controllerAxisShoot2;
    }

    public int getControllerAxisAbility() {
        return controllerAxisAbility;
    }

    public void setControllerAxisAbility(int controllerAxisAbility) {
        this.controllerAxisAbility = controllerAxisAbility;
    }

    public int getControllerAxisLeftRight() {
        return controllerAxisLeftRight;
    }

    public void setControllerAxisLeftRight(int controllerAxisLeftRight) {
        this.controllerAxisLeftRight = controllerAxisLeftRight;
    }

    public int getControllerAxisUpDown() {
        return controllerAxisUpDown;
    }

    public void setControllerAxisUpDown(int controllerAxisUpDown) {
        this.controllerAxisUpDown = controllerAxisUpDown;
    }

    public int getControllerButtonShoot() {
        return controllerButtonShoot;
    }

    public void setControllerButtonShoot(int controllerButtonShoot) {
        this.controllerButtonShoot = controllerButtonShoot;
    }

    public int getControllerButtonShoot2() {
        return controllerButtonShoot2;
    }

    public void setControllerButtonShoot2(int controllerButtonShoot2) {
        this.controllerButtonShoot2 = controllerButtonShoot2;
    }

    public int getControllerButtonAbility() {
        return controllerButtonAbility;
    }

    public void setControllerButtonAbility(int controllerButtonAbility) {
        this.controllerButtonAbility = controllerButtonAbility;
    }

    public int getControllerButtonLeft() {
        return controllerButtonLeft;
    }

    public void setControllerButtonLeft(int controllerButtonLeft) {
        this.controllerButtonLeft = controllerButtonLeft;
    }

    public int getControllerButtonRight() {
        return controllerButtonRight;
    }

    public void setControllerButtonRight(int controllerButtonRight) {
        this.controllerButtonRight = controllerButtonRight;
    }

    public int getControllerButtonUp() {
        return controllerButtonUp;
    }

    public void setControllerButtonUp(int controllerButtonUp) {
        this.controllerButtonUp = controllerButtonUp;
    }

    public boolean isControllerAxisLeftRightInverted() {
        return isControllerAxisLeftRightInverted;
    }

    public boolean isControllerAxisUpDownInverted() {
        return isControllerAxisUpDownInverted;
    }

    public void setIsControllerAxisLeftRightInverted(boolean isControllerAxisLeftRightInverted) {
        this.isControllerAxisLeftRightInverted = isControllerAxisLeftRightInverted;
    }

    public void setIsControllerAxisUpDownInverted(boolean isControllerAxisUpDownInverted) {
        this.isControllerAxisUpDownInverted = isControllerAxisUpDownInverted;
    }

    public int getControllerButtonDown() {
        return controllerButtonDown;
    }

    public void setControllerButtonDown(int controllerButtonDown) {
        this.controllerButtonDown = controllerButtonDown;
    }

    public int getMapScrollSpeed() {
        return mapScrollSpeed;
    }

    public void setMapScrollSpeed(int mapScrollSpeed) {
        this.mapScrollSpeed = mapScrollSpeed;
    }

    public float getNuiUiScale() {
        return nuiUiScale;
    }

    public void setNuiUiScale(float nuiUiScale) {
        this.nuiUiScale = nuiUiScale;
    }
}
