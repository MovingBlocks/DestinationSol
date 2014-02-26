package com.miloshpetrov.sol2.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.miloshpetrov.sol2.SolAppListener;
import com.miloshpetrov.sol2.game.DebugAspects;
import com.miloshpetrov.sol2.menu.GameOptions;
import com.miloshpetrov.sol2.soundtest.SoundTestListener;

public class SolDesktop {
  public static void main(String[] argv) {
    if (false) {
      new LwjglApplication(new SoundTestListener(), "sound test", 800, 600, false);
      return;
    }

    DebugAspects.read();
    GameOptions d = new GameOptions();

    LwjglApplicationConfiguration c = new LwjglApplicationConfiguration();
    c.width = d.x;
    c.height = d.y;
    c.fullscreen = d.fullscreen;
    c.title = "Sol";
    if (DebugAspects.DEV_ROOT_PATH == null) {
      c.addIcon("res/icon.png", Files.FileType.Internal);
    } else {
      c.addIcon(DebugAspects.DEV_ROOT_PATH + "res/icon.png", Files.FileType.Absolute);
    }

    if (DebugAspects.MOBILE) {
      c.width = 640;
      c.height = 480;
      c.fullscreen = false;
    }

    new LwjglApplication(new SolAppListener(), c);
  }

}
