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

  public void save() {
    IniReader.write(FILE_NAME, "x", x, "y", y, "fullscreen", fullscreen, "controlType", controlType, "vol", volMul,
            "keyUpMouse", keyUpMouse, "keyDownMouse", keyDownMouse, "keyUp", keyUp, "keyDown", keyDown,
            "keyLeft", keyLeft, "keyRight", keyRight, "keyShoot", keyShoot, "keyShoot2", keyShoot2,
            "keyAbility", keyAbility);
  }

  public int getKeyUpMouse() {
    return Input.Keys.valueOf(keyUpMouse);
  }

  public int getKeyDownMouse() {
    return Input.Keys.valueOf(keyDownMouse);
  }

  public int getKeyUp() {
    return Input.Keys.valueOf(keyUp);
  }

  public int getKeyDown() {
    return Input.Keys.valueOf(keyDown);
  }

  public int getKeyLeft() {
    return Input.Keys.valueOf(keyLeft);
  }

  public int getKeyRight() {
    return Input.Keys.valueOf(keyRight);
  }

  public int getKeyShoot() {
    return Input.Keys.valueOf(keyShoot);
  }

  public int getKeyShoot2() {
    return Input.Keys.valueOf(keyShoot2);
  }

  public int getKeyAbility() {
    return Input.Keys.valueOf(keyAbility);
  }
}
