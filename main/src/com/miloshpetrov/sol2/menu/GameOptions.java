package com.miloshpetrov.sol2.menu;

public class GameOptions {
  public static final String FILE_NAME = "settings.ini";

  public int x;
  public int y;
  public boolean fullscreen;

  public GameOptions() {
    IniReader r = new IniReader(FILE_NAME);
    x = r.i("x", 800);
    y = r.i("y", 600);
    fullscreen = r.b("fullscreen", false);
  }

  public void advanceReso() {
    if (x == 800) {
      x = 1024;
      y = 768;
      return;
    }
    if (x == 1024) {
      x = 1366;
      y = 768;
      return;
    }
    x = 800;
    y = 600;
  }

  public void save() {
    IniReader.write(FILE_NAME, "x", x, "y", y, "fullscreen", fullscreen);
  }
}
