package com.miloshpetrov.sol2;

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

  public int x;
  public int y;
  public boolean fullscreen;
  public int controlType;
  public float volMul;
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

  private SortedSet<String> supportedResolutions = new TreeSet<String>();
  private Iterator<String> resolutionIterator = null;

  public GameOptions(boolean mobile, SolFileReader reader) {
    IniReader r = new IniReader(FILE_NAME, reader, false);
    x = r.i("x", 800);
    y = r.i("y", 600);
    fullscreen = r.b("fullscreen", false);
    controlType = mobile ? CONTROL_KB : r.i("controlType", CONTROL_MIXED);
    volMul = r.f("vol", 1);
    keyUpMouseName = r.s("keyUpMouse", "W");
    keyDownMouseName = r.s("keyDownMouse", "S");
    keyUpName = r.s("keyUp", "Up");
    keyDownName = r.s("keyDown", "Down");
    keyLeftName = r.s("keyLeft", "Left");
    keyRightName = r.s("keyRight", "Right");
    keyShootName = r.s("keyShoot", "Space");
    keyShoot2Name = r.s("keyShoot2", "L-Ctrl");
    keyAbilityName = r.s("keyAbility", "L-Shift");
    keyEscapeName = r.s("keyEscape", "Escape");
    keyMapName = r.s("keyMap", "Tab");
    keyInventoryName = r.s("keyInventory", "I");
    keyTalkName = r.s("keyTalk", "T");
    keyPauseName = r.s("keyPause", "P");
    keyDropName = r.s("keyDrop", "D");
    keySellMenuName = r.s("keySellMenu", "S");
    keyBuyMenuName = r.s("keyBuyMenu", "B");
    keyChangeShipMenuName = r.s("keyChangeShipMenu", "C");
    keyHireShipMenuName = r.s("keyHireShipMenu", "H");
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

  public void advanceVolMul() {
    if (volMul == 0) {
      volMul = .33f;
    } else if (volMul < .4f) {
      volMul = .66f;
    } else if (volMul < .7f) {
      volMul = 1;
    } else {
      volMul = 0;
    }
    save();
  }

  /**
   * Save the configuration settings to file.
   */
  public void save() {
    IniReader.write(FILE_NAME, "x", x, "y", y, "fullscreen", fullscreen, "controlType", controlType, "vol", volMul,
            "keyUpMouse", getKeyUpMouseName(), "keyDownMouse", getKeyDownMouseName(), "keyUp", getKeyUpName(), "keyDown", keyDownName,
            "keyLeft", keyLeftName, "keyRight", keyRightName, "keyShoot", keyShootName, "keyShoot2", getKeyShoot2Name(),
            "keyAbility", getKeyAbilityName(), "keyEscape", getKeyEscapeName(), "keyMap", keyMapName, "keyInventory", keyInventoryName,
            "keyTalk", getKeyTalkName(), "keyPause", getKeyPauseName(), "keyDrop", getKeyDropName(), "keySellMenu", getKeySellMenuName(),
            "keyBuyMenu", getKeyBuyMenuName(), "keyChangeShipMenu", getKeyChangeShipMenuName(), "keyHireShipMenu", getKeyHireShipMenuName());
  }

  /**
   * Get the defined key for up when the input controlType is set to CONTROL_MIXED. This includes navigating down in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyUpMouse() {
    return Input.Keys.valueOf(getKeyUpMouseName());
  }

  /**
   * Get the defined key for down when the input controlType is set to CONTROL_MIXED.
   * This includes activating the ship's thrusters and navigating up in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyDownMouse() {
    return Input.Keys.valueOf(getKeyDownMouseName());
  }

  /**
   * Get the readable name of the defined key for up when the input controlType is set to CONTROL_MIXED.
   * This includes navigating down in menus.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyUpMouseName() {
    return keyUpMouseName;
  }

  /**
   * Get the readable name of the defined key for down when the input controlType is set to CONTROL_MIXED.
   * This includes navigating down in menus.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyDownMouseName() {
    return keyDownMouseName;
  }

  /**
   * Get the defined key for up. This includes activating the ship's thrusters and navigating up in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyUp() {
    return Input.Keys.valueOf(getKeyUpName());
  }

  /**
   * Get the readable name of the defined key for  up.
   * This includes activating the ship's thrusters and navigating up in menus.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyUpName() {
    return keyUpName;
  }

  /**
   * Get the defined key for down. This includes navigating down in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyDown() {
    return Input.Keys.valueOf(keyDownName);
  }

  /**
   * Get the readable name of the defined key for down.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyDownName() {
    return keyDownName;
  }

  /**
   * Get the defined key for left. This includes rotating the ship left and navigating left in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyLeft() {
    return Input.Keys.valueOf(keyLeftName);
  }

  /**
   * Get the readable name of the defined key for left.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyLeftName() {
    return keyLeftName;
  }

  /**
   * Get the defined key for right. This includes rotating the ship right and navigating right in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyRight() {
    return Input.Keys.valueOf(keyRightName);
  }

  /**
   * Get the readable name of the defined key for right.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyRightName() {
    return keyRightName;
  }

  /**
   * Get the defined key for shooting the primary weapon.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyShoot() {
    return Input.Keys.valueOf(keyShootName);
  }

  /**
   * Get the readable name of the defined key for shooting the primary weapon.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyShootName() {
    return keyShootName;
  }

  /**
   * Get the defined key for shooting the secondary weapon.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyShoot2() {
    return Input.Keys.valueOf(getKeyShoot2Name());
  }

  /**
   * Get the readable name of the defined key for shooting the secondary weapon.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyShoot2Name() {
    return keyShoot2Name;
  }

  /**
   * Get the defined key for equipping and unequipping the primary weapon.
   * This is currently set to the same key as keyShootName
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyEquip() {
    return Input.Keys.valueOf(keyShootName);
  }

  /**
   * Get the defined key for equipping and unequipping the primary weapon.
   * This is currently set to the same key as keyShootName
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyEquipName() {
    return getKeyShootName();
  }

  /**
   * Get the defined key for equipping and unequipping the secondary weapon.
   * This is currently set to the same key as keyShoot2Name
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyEquip2() {
    return Input.Keys.valueOf(getKeyShoot2Name());
  }

  /**
   * Get the defined key for activating the ship's special ability.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyAbility() {
    return Input.Keys.valueOf(getKeyAbilityName());
  }


  /**
   * Get the readable name of the defined key for activating the ship's special ability.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyAbilityName() {
    return keyAbilityName;
  }

  /**
   * Get the defined key for escape. This includes bringing up the in-game menu and exiting in-game menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyEscape() {
    return Input.Keys.valueOf(getKeyEscapeName());
  }

  /**
   * Get the readable name of the defined key for escape.
   * This includes bringing up the in-game menu and exiting in-game menus.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyEscapeName() {
    return keyEscapeName;
  }

  /**
   * Get the defined key for activating the menu.
   * This is currently set to the same key as KeyEscape
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyMenu() {
    return getKeyEscape();
  }

  /**
   * Get the defined key for closing the menu.
   * This is currently set to the same key as KeyEscape
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyClose() {
    return getKeyEscape();
  }

  /**
   * Get the readable name of the defined key for closing the menu
   * This is currently set to the same key as KeyEscape
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyCloseName() {
    return getKeyEscapeName();
  }

  /**
   * Get the defined key for buying items.
   * This is currently set to the same key as KeyShoot
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyBuyItem() {
    return getKeyShoot();
  }

  /**
   * Get the defined key for buying items.
   * This is currently set to the same key as KeyShoot
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyBuyItemName() {
    return getKeyShootName();
  }

  /**
   * Get the defined key for selling items.
   * This is currently set to the same key as KeyShoot
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeySellItem() {
    return getKeyShoot();
  }

  /**
   * Get the defined key for hiring ships.
   * This is currently set to the same key as KeyShoot
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyHireShip() {
    return getKeyShoot();
  }

  /**
   * Get the defined key for changing ships.
   * This is currently set to the same key as KeyShoot
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyChangeShip() {
    return getKeyShoot();
  }

  /**
   * Get the defined key for zooming out on the map.
   * This is currently set to the same key as KeyUp
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyZoomIn() {
    return getKeyUp();
  }

  /**
   * Get the readable name of the defined key for zooming out on the map.
   * This is currently set to the same key as KeyUp
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyZoomInName() {
    return getKeyUpName();
  }

  /**
   * Get the defined key for zooming out on the map.
   * This is currently set to the same key as KeyDown
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyZoomOut() {
    return getKeyDown();
  }

  /**
   * Get the defined key for opening and closing the map.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyMap() {
    return Input.Keys.valueOf(keyMapName);
  }

  /**
   * Get the readable name of the defined key for opening and closing the map.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyMapName() {
    return keyMapName;
  }

  /**
   * Get the defined key for opening the inventory.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyInventory() {
    return Input.Keys.valueOf(keyInventoryName);
  }

  /**
   * Get the readable name of the defined key for opening the inventory.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyInventoryName() {
    return keyInventoryName;
  }

  /**
   * Get the defined key for opening the talk menu.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyTalk() {
    return Input.Keys.valueOf(getKeyTalkName());
  }

  /**
   * Get the readable name of the defined key for opening the talk menu.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyTalkName() {
    return keyTalkName;
  }

  /**
   * Get the defined key for pausing and continuing the game.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyPause() {
    return Input.Keys.valueOf(getKeyPauseName());
  }

  /**
   * Get the readable name of the defined key for pausing and continuing the game.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyPauseName() {
    return keyPauseName;
  }

  /**
   * Get the defined key for dropping items.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyDrop() {
    return Input.Keys.valueOf(getKeyDropName());
  }

  /**
   * Get the readable name of the defined key for dropping items.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyDropName() {
    return keyDropName;
  }

  /**
   * Get the defined key for the sell menu.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeySellMenu() {
    return Input.Keys.valueOf(getKeySellMenuName());
  }

  /**
   * Get the readable name of the defined key for the sell menu.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeySellMenuName() {
    return keySellMenuName;
  }

  /**
   * Get the defined key for the buy menu.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyBuyMenu() {
    return Input.Keys.valueOf(getKeyBuyMenuName());
  }

  /**
   * Get the readable name of the defined key for the buy menu.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyBuyMenuName() {
    return keyBuyMenuName;
  }

  /**
   * Get the defined key for the change ship menu.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyChangeShipMenu() {
    return Input.Keys.valueOf(getKeyChangeShipMenuName());
  }

  /**
   * Get the readable name of the defined key for the change ship menu.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyChangeShipMenuName() {
    return keyChangeShipMenuName;
  }

  /**
   * Get the defined key for the hire ship menu.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyHireShipMenu() {
    return Input.Keys.valueOf(getKeyHireShipMenuName());
  }

  /**
   * Get the readable name of the defined key for the hire ship menu.
   * @return String The readable name as defined in Input.Keys
   */
  public String getKeyHireShipMenuName() {
    return keyHireShipMenuName;
  }
}
