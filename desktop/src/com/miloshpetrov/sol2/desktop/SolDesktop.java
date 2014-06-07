package com.miloshpetrov.sol2.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.miloshpetrov.sol2.SolAppListener;
import com.miloshpetrov.sol2.game.DebugOptions;
import com.miloshpetrov.sol2.menu.GameOptions;
import com.miloshpetrov.sol2.soundtest.SoundTestListener;

import java.nio.file.Paths;

public class SolDesktop {
  public static void main(String[] argv) {
    if (false) {
      new LwjglApplication(new SoundTestListener(), "sound test", 800, 600, false);
      return;
    }

    boolean devBuild = java.nio.file.Files.exists(Paths.get("devBuild"));
    if (devBuild) DebugOptions.DEV_ROOT_PATH = "../trunk/main/"; // supposing that solWin is in the same directory where trunk is.
    DebugOptions.read(false);

    LwjglApplicationConfiguration c = new LwjglApplicationConfiguration();
    if (DebugOptions.EMULATE_MOBILE) {
      c.width = 640;
      c.height = 480;
      c.fullscreen = false;
    } else {
      GameOptions d = new GameOptions(false, false);
      c.width = d.x;
      c.height = d.y;
      c.fullscreen = d.fullscreen;
    }

    c.title = "Sol";
    if (DebugOptions.DEV_ROOT_PATH == null) {
      c.addIcon("res/icon.png", Files.FileType.Internal);
    } else {
      c.addIcon(DebugOptions.DEV_ROOT_PATH + "res/icon.png", Files.FileType.Absolute);
    }

    new LwjglApplication(new SolAppListener(), c);
  }

}
