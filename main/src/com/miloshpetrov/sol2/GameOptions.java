package com.miloshpetrov.sol2;

import com.badlogic.gdx.Input;

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
  private String keyUpMouse;
  private String keyDownMouse;
  private String keyUp;
  private String keyDown;
  private String keyLeft;
  private String keyRight;
  private String keyShoot;
  private String keyShoot2;
  private String keyAbility;
  private String keyEscape;
  private String keyMap;


  public GameOptions(boolean mobile, SolFileReader reader) {
    IniReader r = new IniReader(FILE_NAME, reader, false);
    x = r.i("x", 800);
    y = r.i("y", 600);
    fullscreen = r.b("fullscreen", false);
    controlType = mobile ? CONTROL_KB : r.i("controlType", CONTROL_MIXED);
    volMul = r.f("vol", 1);
    keyUpMouse = r.s("keyUpMouse", "W");
    keyDownMouse = r.s("keyDownMouse", "S");
    keyUp = r.s("keyUp", ("Up"));
    keyDown = r.s("keyDown", ("Down"));
    keyLeft = r.s("keyLeft", "Left");
    keyRight = r.s("keyRight", "Right");
    keyShoot = r.s("keyShoot", "Space");
    keyShoot2 = r.s("keyShoot2", "L-Ctrl");
    keyAbility = r.s("keyAbility", "L-Shift");
    keyEscape = r.s("keyEscape", "Escape");
    keyMap = r.s("keyMap", "Tab");
  }

  public void advanceReso() {
    if (x == 800) {
      x = 1024;
      y = 768;
    } else if (x == 1024) {
      x = 1366;
      y = 768;
    } else {
      x = 800;
      y = 600;
    }
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
            "keyUpMouse", keyUpMouse, "keyDownMouse", keyDownMouse, "keyUp", keyUp, "keyDown", keyDown,
            "keyLeft", keyLeft, "keyRight", keyRight, "keyShoot", keyShoot, "keyShoot2", keyShoot2,
            "keyAbility", keyAbility, "keyEscape", keyEscape, "keyMap", keyMap);
  }

  /**
   * Get the defined key for up when the input controlType is set to CONTROL_MIXED. This includes navigating down in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyUpMouse() {
    return Input.Keys.valueOf(keyUpMouse);
  }

  /**
   * Get the defined key for down when the input controlType is set to CONTROL_MIXED.
   * This includes activating the ship's thrusters and navigating up in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyDownMouse() {
    return Input.Keys.valueOf(keyDownMouse);
  }

  /**
   * Get the defined key for up. This includes activating the ship's thrusters and navigating up in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyUp() {
    return Input.Keys.valueOf(keyUp);
  }

  /**
   * Get the defined key for down. This includes navigating down in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyDown() {
    return Input.Keys.valueOf(keyDown);
  }

  /**
   * Get the defined key for left. This includes rotating the ship left and navigating left in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyLeft() {
    return Input.Keys.valueOf(keyLeft);
  }

  /**
   * Get the defined key for right. This includes rotating the ship right and navigating right in menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyRight() {
    return Input.Keys.valueOf(keyRight);
  }

  /**
   * Get the defined key for shooting the primary weapon.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyShoot() {
    return Input.Keys.valueOf(keyShoot);
  }

  /**
   * Get the defined key for shooting the secondary weapon.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyShoot2() {
    return Input.Keys.valueOf(keyShoot2);
  }

  /**
   * Get the defined key for activating the ship's special ability.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyAbility() {
    return Input.Keys.valueOf(keyAbility);
  }

  /**
   * Get the defined key for escape. This includes bringing up the in-game menu and exiting in-game menus.
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyEscape() {
    return Input.Keys.valueOf(keyEscape);
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
   * Get the defined key for buying items.
   * This is currently set to the same key as KeyShoot
   * @return int The keycode as defined in Input.Keys
   */
  public int getKeyBuyItem() {
    return getKeyShoot();
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
    return Input.Keys.valueOf(keyMap);
  }
}
