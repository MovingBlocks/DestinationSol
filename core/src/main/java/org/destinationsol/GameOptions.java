package org.destinationsol;

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

  public GameOptions(boolean mobile, SolFileReader reader) {
    IniReader r = new IniReader(FILE_NAME, reader, false);
    x = r.i("x", 800);
    y = r.i("y", 600);
    fullscreen = r.b("fullscreen", false);
    controlType = mobile ? CONTROL_KB : r.i("controlType", CONTROL_MIXED);
    volMul = r.f("vol", 1);
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
    IniReader.write(FILE_NAME, "x", x, "y", y, "fullscreen", fullscreen, "controlType", controlType, "vol", volMul);
  }
}
