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
package org.destinationsol;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class GameOptions {
    public static final String FILE_NAME = "settings.ini";
    public static final int CONTROL_KB = 0;
    public static final int CONTROL_MIXED = 1;
    public static final int CONTROL_MOUSE = 2;
    public static final int CONTROL_CONTROLLER = 3;
    public static final String DEFAULT_MOUSE_UP = "W";
    public static final String DEFAULT_MOUSE_DOWN = "S";
    public static final String DEFAULT_UP = "Up";
    public static final String DEFAULT_DOWN = "Down";
    public static final String DEFAULT_LEFT = "Left";
    public static final String DEFAULT_RIGHT = "Right";
    public static final String DEFAULT_SHOOT = "Space";
    public static final String DEFAULT_SHOOT2 = "L-Ctrl";
    public static final String DEFAULT_ABILITY = "L-Shift";
    public static final String DEFAULT_ESCAPE = "Escape";
    public static final String DEFAULT_MAP = "Tab";
    public static final String DEFAULT_INVENTORY = "I";
    public static final String DEFAULT_TALK = "T";
    public static final String DEFAULT_PAUSE = "P";
    public static final String DEFAULT_DROP = "D";
    public static final String DEFAULT_SELL = "S";
    public static final String DEFAULT_BUY = "B";
    public static final String DEFAULT_CHANGE_SHIP = "C";
    public static final String DEFAULT_HIRE_SHIP = "H";
    public static final int DEFAULT_AXIS_SHOOT = 1;
    public static final int DEFAULT_AXIS_SHOOT2 = 0;
    public static final int DEFAULT_AXIS_ABILITY = -1;
    public static final int DEFAULT_AXIS_LEFT_RIGHT = 2;
    public static final boolean DEFAULT_AXIS_LEFT_RIGHT_INVERTED_ = false;
    public static final int DEFAULT_AXIS_UP_DOWN = 5;
    public static final boolean DEFAULT_AXIS_UP_DOWN_INVERTED_ = false;
    public static final int DEFAULT_BUTTON_SHOOT = -1;
    public static final int DEFAULT_BUTTON_SHOOT2 = -1;
    public static final int DEFAULT_BUTTON_ABILITY = 14;
    public static final int DEFAULT_BUTTON_UP = -1;
    public static final int DEFAULT_BUTTON_DOWN = -1;
    public static final int DEFAULT_BUTTON_LEFT = -1;
    public static final int DEFAULT_BUTTON_RIGHT = -1;

    public int x;
    public int y;
    public boolean fullscreen;
    public int controlType;
    public float sfxVolumeMultiplier;
    public float musicVolumeMultiplier;
    public boolean canSellEquippedItems;
    private String keyUpMouseName;
    private String keyDownMouseName;
    private String keyUpName;
    private String keyDownName;
    private String keyLeftName;
    private String keyRightName;
    private String keyShootName;
    private String keyShoot2Name;
    private String keyAbilityName;
    private String keyEscapeName;
    private String keyMapName;
    private String keyInventoryName;
    private String keyTalkName;
    private String keyPauseName;
    private String keyDropName;
    private String keySellMenuName;
    private String keyBuyMenuName;
    private String keyChangeShipMenuName;
    private String keyHireShipMenuName;
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

    private SortedSet<String> supportedResolutions = new TreeSet<>();
    private Iterator<String> resolutionIterator = null;

    public GameOptions(boolean mobile, SolFileReader reader) {
        IniReader r = new IniReader(FILE_NAME, reader);
        x = r.getInt("x", 800);
        y = r.getInt("y", 600);
        fullscreen = r.getBoolean("fullscreen", false);
        controlType = mobile ? CONTROL_KB : r.getInt("controlType", CONTROL_MIXED);
        sfxVolumeMultiplier = r.getFloat("sfxVol", 1);
        musicVolumeMultiplier = r.getFloat("musicVol", 1);
        keyUpMouseName = r.getString("keyUpMouse", DEFAULT_MOUSE_UP);
        keyDownMouseName = r.getString("keyDownMouse", DEFAULT_MOUSE_DOWN);
        keyUpName = r.getString("keyUp", DEFAULT_UP);
        keyDownName = r.getString("keyDown", DEFAULT_DOWN);
        keyLeftName = r.getString("keyLeft", DEFAULT_LEFT);
        keyRightName = r.getString("keyRight", DEFAULT_RIGHT);
        keyShootName = r.getString("keyShoot", DEFAULT_SHOOT);
        keyShoot2Name = r.getString("keyShoot2", DEFAULT_SHOOT2);
        keyAbilityName = r.getString("keyAbility", DEFAULT_ABILITY);
        keyEscapeName = r.getString("keyEscape", DEFAULT_ESCAPE);
        keyMapName = r.getString("keyMap", DEFAULT_MAP);
        keyInventoryName = r.getString("keyInventory", DEFAULT_INVENTORY);
        keyTalkName = r.getString("keyTalk", DEFAULT_TALK);
        keyPauseName = r.getString("keyPause", DEFAULT_PAUSE);
        keyDropName = r.getString("keyDrop", DEFAULT_DROP);
        keySellMenuName = r.getString("keySellMenu", DEFAULT_SELL);
        keyBuyMenuName = r.getString("keyBuyMenu", DEFAULT_BUY);
        keyChangeShipMenuName = r.getString("keyChangeShipMenu", DEFAULT_CHANGE_SHIP);
        keyHireShipMenuName = r.getString("keyHireShipMenu", DEFAULT_HIRE_SHIP);
        controllerAxisShoot = r.getInt("controllerAxisShoot", DEFAULT_AXIS_SHOOT);
        controllerAxisShoot2 = r.getInt("controllerAxisShoot2", DEFAULT_AXIS_SHOOT2);
        controllerAxisAbility = r.getInt("controllerAxisAbility", DEFAULT_AXIS_ABILITY);
        controllerAxisLeftRight = r.getInt("controllerAxisLeftRight", DEFAULT_AXIS_LEFT_RIGHT);
        isControllerAxisLeftRightInverted = r.getBoolean("isControllerAxisLeftRightInverted", DEFAULT_AXIS_LEFT_RIGHT_INVERTED_);
        controllerAxisUpDown = r.getInt("controllerAxisUpDown", DEFAULT_AXIS_UP_DOWN);
        isControllerAxisUpDownInverted = r.getBoolean("isControllerAxisUpDownInverted", DEFAULT_AXIS_UP_DOWN_INVERTED_);
        controllerButtonShoot = r.getInt("controllerButtonShoot", DEFAULT_BUTTON_SHOOT);
        controllerButtonShoot2 = r.getInt("controllerButtonShoot2", DEFAULT_BUTTON_SHOOT2);
        controllerButtonAbility = r.getInt("controllerButtonAbility", DEFAULT_BUTTON_ABILITY);
        controllerButtonLeft = r.getInt("controllerButtonLeft", DEFAULT_BUTTON_LEFT);
        controllerButtonRight = r.getInt("controllerButtonRight", DEFAULT_BUTTON_RIGHT);
        controllerButtonUp = r.getInt("controllerButtonUp", DEFAULT_BUTTON_UP);
        controllerButtonDown = r.getInt("controllerButtonDown", DEFAULT_BUTTON_DOWN);
        canSellEquippedItems = r.getBoolean("canSellEquippedItems", false);
    }

    public void advanceReso() {
        if (resolutionIterator == null) {
            // Initialize resolution choices - get the resolutions that are supported
            Graphics.DisplayMode displayModes[] = Gdx.graphics.getDisplayModes();

            for (Graphics.DisplayMode d : displayModes) {
                supportedResolutions.add(d.width + "x" + d.height);
            }

            resolutionIterator = supportedResolutions.iterator();
        }

        String nextResolution;
        if (resolutionIterator.hasNext()) {
            nextResolution = resolutionIterator.next();
        } else {
            // Probably somehow possible to get no entries at all which would crash, but then we're doomed anyway
            resolutionIterator = supportedResolutions.iterator();
            nextResolution = resolutionIterator.next();
        }

        // TODO: Probably should validate, but then there are still many things we should probably add! :-)
        x = Integer.parseInt(nextResolution.substring(0, nextResolution.indexOf("x")));
        y = Integer.parseInt(nextResolution.substring(nextResolution.indexOf("x") + 1, nextResolution.length()));

        save();
    }

    public void advanceControlType(boolean mobile) {
        if (controlType == CONTROL_KB) {
            controlType = mobile ? CONTROL_MOUSE : CONTROL_MIXED;
        } else if (controlType == CONTROL_MIXED) {
            controlType = CONTROL_CONTROLLER;
            //    } else if (controlType == CONTROL_MIXED) {
            //      controlType = CONTROL_MOUSE;
        } else {
            controlType = CONTROL_KB;
        }
        save();
    }

    public void advanceFullscreen() {
        fullscreen = !fullscreen;
        save();
    }

    public void advanceSoundVolMul() {
        if (sfxVolumeMultiplier == 0.f) {
            sfxVolumeMultiplier = 0.25f;
        } else if (sfxVolumeMultiplier == 0.25f) {
            sfxVolumeMultiplier = 0.5f;
        } else if (sfxVolumeMultiplier == 0.5f) {
            sfxVolumeMultiplier = 0.75f;
        } else if (sfxVolumeMultiplier == 0.75f) {
            sfxVolumeMultiplier = 1.f;
        } else {
            sfxVolumeMultiplier = 0.f;
        }
        save();
    }

    public void advanceMusicVolMul() {
        if (musicVolumeMultiplier == 0.f) {
            musicVolumeMultiplier = 0.25f;
        } else if (musicVolumeMultiplier == 0.25f) {
            musicVolumeMultiplier = 0.5f;
        } else if (musicVolumeMultiplier == 0.5f) {
            musicVolumeMultiplier = 0.75f;
        } else if (musicVolumeMultiplier == 0.75f) {
            musicVolumeMultiplier = 1.f;
        } else {
            musicVolumeMultiplier = 0.f;
        }
        save();
    }

    /**
     * Save the configuration settings to file.
     */
    public void save() {
        IniReader.write(FILE_NAME, "x", x, "y", y, "fullscreen", fullscreen, "controlType", controlType, "sfxVol", sfxVolumeMultiplier, "musicVol", musicVolumeMultiplier,
                "canSellEquippedItems", canSellEquippedItems,
                "keyUpMouse", getKeyUpMouseName(), "keyDownMouse", getKeyDownMouseName(), "keyUp", getKeyUpName(), "keyDown", keyDownName,
                "keyLeft", keyLeftName, "keyRight", keyRightName, "keyShoot", keyShootName, "keyShoot2", getKeyShoot2Name(),
                "keyAbility", getKeyAbilityName(), "keyEscape", getKeyEscapeName(), "keyMap", keyMapName, "keyInventory", keyInventoryName,
                "keyTalk", getKeyTalkName(), "keyPause", getKeyPauseName(), "keyDrop", getKeyDropName(), "keySellMenu", getKeySellMenuName(),
                "keyBuyMenu", getKeyBuyMenuName(), "keyChangeShipMenu", getKeyChangeShipMenuName(), "keyHireShipMenu", getKeyHireShipMenuName(),
                "controllerAxisShoot", getControllerAxisShoot(), "controllerAxisShoot2", getControllerAxisShoot2(),
                "controllerAxisAbility", getControllerAxisAbility(), "controllerAxisLeftRight", getControllerAxisLeftRight(),
                "isControllerAxisLeftRightInverted", isControllerAxisLeftRightInverted(), "controllerAxisUpDown", getControllerAxisUpDown(),
                "isControllerAxisUpDownInverted", isControllerAxisUpDownInverted(), "controllerButtonShoot", getControllerButtonShoot(),
                "controllerButtonShoot2", getControllerButtonShoot2(), "controllerButtonAbility", getControllerButtonAbility(),
                "controllerButtonLeft", getControllerButtonLeft(), "controllerButtonRight", getControllerButtonRight(),
                "controllerButtonUp", getControllerButtonUp(), "controllerButtonDown", getControllerButtonDown());
    }

    /**
     * Get the defined key for up when the input controlType is set to CONTROL_MIXED. This includes navigating down in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyUpMouse() {
        return Input.Keys.valueOf(getKeyUpMouseName());
    }

    /**
     * Get the defined key for down when the input controlType is set to CONTROL_MIXED.
     * This includes activating the ship's thrusters and navigating up in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyDownMouse() {
        return Input.Keys.valueOf(getKeyDownMouseName());
    }

    /**
     * Get the readable name of the defined key for up when the input controlType is set to CONTROL_MIXED.
     * This includes navigating down in menus.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyUpMouseName() {
        return keyUpMouseName;
    }

    public void setKeyUpMouseName(String keyUpMouseName) {
        this.keyUpMouseName = keyUpMouseName;
    }

    /**
     * Get the readable name of the defined key for down when the input controlType is set to CONTROL_MIXED.
     * This includes navigating down in menus.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyDownMouseName() {
        return keyDownMouseName;
    }

    public void setKeyDownMouseName(String keyDownMouseName) {
        this.keyDownMouseName = keyDownMouseName;
    }

    /**
     * Get the defined key for up. This includes activating the ship's thrusters and navigating up in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyUp() {
        return Input.Keys.valueOf(getKeyUpName());
    }

    /**
     * Get the readable name of the defined key for  up.
     * This includes activating the ship's thrusters and navigating up in menus.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyUpName() {
        return keyUpName;
    }

    public void setKeyUpName(String keyUpName) {
        this.keyUpName = keyUpName;
    }

    /**
     * Get the defined key for down. This includes navigating down in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyDown() {
        return Input.Keys.valueOf(keyDownName);
    }

    /**
     * Get the readable name of the defined key for down.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyDownName() {
        return keyDownName;
    }

    public void setKeyDownName(String keyDownName) {
        this.keyDownName = keyDownName;
    }

    /**
     * Get the defined key for left. This includes rotating the ship left and navigating left in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyLeft() {
        return Input.Keys.valueOf(keyLeftName);
    }

    /**
     * Get the readable name of the defined key for left.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyLeftName() {
        return keyLeftName;
    }

    public void setKeyLeftName(String keyLeftName) {
        this.keyLeftName = keyLeftName;
    }

    /**
     * Get the defined key for right. This includes rotating the ship right and navigating right in menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyRight() {
        return Input.Keys.valueOf(keyRightName);
    }

    /**
     * Get the readable name of the defined key for right.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyRightName() {
        return keyRightName;
    }

    public void setKeyRightName(String keyRightName) {
        this.keyRightName = keyRightName;
    }

    /**
     * Get the defined key for shooting the primary weapon.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyShoot() {
        return Input.Keys.valueOf(keyShootName);
    }

    /**
     * Get the readable name of the defined key for shooting the primary weapon.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyShootName() {
        return keyShootName;
    }

    public void setKeyShootName(String keyShootName) {
        this.keyShootName = keyShootName;
    }

    /**
     * Get the defined key for shooting the secondary weapon.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyShoot2() {
        return Input.Keys.valueOf(getKeyShoot2Name());
    }

    /**
     * Get the readable name of the defined key for shooting the secondary weapon.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyShoot2Name() {
        return keyShoot2Name;
    }

    public void setKeyShoot2Name(String keyShoot2Name) {
        this.keyShoot2Name = keyShoot2Name;
    }

    /**
     * Get the defined key for equipping and unequipping the primary weapon.
     * This is currently set to the same key as keyShootName
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyEquip() {
        return Input.Keys.valueOf(keyShootName);
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
        return Input.Keys.valueOf(getKeyAbilityName());
    }

    /**
     * Get the readable name of the defined key for activating the ship's special ability.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyAbilityName() {
        return keyAbilityName;
    }

    public void setKeyAbilityName(String keyAbilityName) {
        this.keyAbilityName = keyAbilityName;
    }

    /**
     * Get the defined key for escape. This includes bringing up the in-game menu and exiting in-game menus.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyEscape() {
        return Input.Keys.valueOf(getKeyEscapeName());
    }

    /**
     * Get the readable name of the defined key for escape.
     * This includes bringing up the in-game menu and exiting in-game menus.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyEscapeName() {
        return keyEscapeName;
    }

    public void setKeyEscapeName(String keyEscapeName) {
        this.keyEscapeName = keyEscapeName;
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
     * Get the defined key for zooming out on the map.
     * This is currently set to the same key as KeyUp
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyZoomIn() {
        return getKeyUp();
    }

    /**
     * Get the readable name of the defined key for zooming out on the map.
     * This is currently set to the same key as KeyUp
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyZoomInName() {
        return getKeyUpName();
    }

    /**
     * Get the defined key for zooming out on the map.
     * This is currently set to the same key as KeyDown
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyZoomOut() {
        return getKeyDown();
    }

    /**
     * Get the defined key for opening and closing the map.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyMap() {
        return Input.Keys.valueOf(keyMapName);
    }

    /**
     * Get the readable name of the defined key for opening and closing the map.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyMapName() {
        return keyMapName;
    }

    public void setKeyMapName(String keyMapName) {
        this.keyMapName = keyMapName;
    }

    /**
     * Get the defined key for opening the inventory.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyInventory() {
        return Input.Keys.valueOf(keyInventoryName);
    }

    /**
     * Get the readable name of the defined key for opening the inventory.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyInventoryName() {
        return keyInventoryName;
    }

    public void setKeyInventoryName(String keyInventoryName) {
        this.keyInventoryName = keyInventoryName;
    }

    /**
     * Get the defined key for opening the talk menu.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyTalk() {
        return Input.Keys.valueOf(getKeyTalkName());
    }

    /**
     * Get the readable name of the defined key for opening the talk menu.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyTalkName() {
        return keyTalkName;
    }

    public void setKeyTalkName(String keyTalkName) {
        this.keyTalkName = keyTalkName;
    }

    /**
     * Get the defined key for pausing and continuing the game.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyPause() {
        return Input.Keys.valueOf(getKeyPauseName());
    }

    /**
     * Get the readable name of the defined key for pausing and continuing the game.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyPauseName() {
        return keyPauseName;
    }

    public void setKeyPauseName(String keyPauseName) {
        this.keyPauseName = keyPauseName;
    }

    /**
     * Get the defined key for dropping items.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyDrop() {
        return Input.Keys.valueOf(getKeyDropName());
    }

    /**
     * Get the readable name of the defined key for dropping items.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyDropName() {
        return keyDropName;
    }

    public void setKeyDropName(String keyDropName) {
        this.keyDropName = keyDropName;
    }

    /**
     * Get the defined key for the sell menu.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeySellMenu() {
        return Input.Keys.valueOf(getKeySellMenuName());
    }

    /**
     * Get the readable name of the defined key for the sell menu.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeySellMenuName() {
        return keySellMenuName;
    }

    public void setKeySellMenuName(String keySellMenuName) {
        this.keySellMenuName = keySellMenuName;
    }

    /**
     * Get the defined key for the buy menu.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyBuyMenu() {
        return Input.Keys.valueOf(getKeyBuyMenuName());
    }

    /**
     * Get the readable name of the defined key for the buy menu.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyBuyMenuName() {
        return keyBuyMenuName;
    }

    public void setKeyBuyMenuName(String keyBuyMenuName) {
        this.keyBuyMenuName = keyBuyMenuName;
    }

    /**
     * Get the defined key for the change ship menu.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyChangeShipMenu() {
        return Input.Keys.valueOf(getKeyChangeShipMenuName());
    }

    /**
     * Get the readable name of the defined key for the change ship menu.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyChangeShipMenuName() {
        return keyChangeShipMenuName;
    }

    public void setKeyChangeShipMenuName(String keyChangeShipMenuName) {
        this.keyChangeShipMenuName = keyChangeShipMenuName;
    }

    /**
     * Get the defined key for the hire ship menu.
     *
     * @return int The keycode as defined in Input.Keys
     */
    public int getKeyHireShipMenu() {
        return Input.Keys.valueOf(getKeyHireShipMenuName());
    }

    /**
     * Get the readable name of the defined key for the hire ship menu.
     *
     * @return String The readable name as defined in Input.Keys
     */
    public String getKeyHireShipMenuName() {
        return keyHireShipMenuName;
    }

    public void setKeyHireShipMenuName(String keyHireShipMenuName) {
        this.keyHireShipMenuName = keyHireShipMenuName;
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

    public String getSFXVolumeAsText() {
        if (sfxVolumeMultiplier == 0.f) {
            return "Off";
        }
        if (sfxVolumeMultiplier == 0.25f) {
            return "Low";
        }
        if (sfxVolumeMultiplier == 0.5f) {
            return "Medium";
        }
        if (sfxVolumeMultiplier == 0.75f) {
            return "High";
        }
        return "Max";
    }

    public String getMusicVolumeAsText() {
        if (musicVolumeMultiplier == 0.f) {
            return "Off";
        }
        if (musicVolumeMultiplier == 0.25f) {
            return "Low";
        }
        if (musicVolumeMultiplier == 0.5f) {
            return "Medium";
        }
        if (musicVolumeMultiplier == 0.75f) {
            return "High";
        }
        return "Max";
    }
}
