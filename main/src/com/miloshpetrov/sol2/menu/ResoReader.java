package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResoReader {
  private ResoReader() {}

  public static Data read() {
    Data r = new Data(800, 600, false);

    String lines = "";
    try {
      byte[] encoded = Files.readAllBytes(Paths.get("graphics.ini"));
      lines = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
    } catch (IOException ignore) {
    }

    File file = new File("graphics.ini");
    if (file.exists()) {
      for (String line : lines.split("\n")) {
        String[] sides = line.split("=");
        if (sides.length < 2) continue;
        String key = sides[0].trim();
        String val = sides[1].trim();
        if ("x".equals(key)) {
          r.x = Integer.parseInt(val);
        }
        if ("y".equals(key)) {
          r.y = Integer.parseInt(val);
        }
        if ("fullscreen".equals(key)) {
          r.fs = Boolean.parseBoolean(val);
        }
      }

    }
    return r;
  }

  public static void write(Data d) {
    String val = "x=" + d.x + "\ny=" + d.y + "\nfullscreen=" + d.fs;
    FileHandle file = Gdx.files.local("graphics.ini");
    file.writeString(val, false);
  }

  public static class Data {
    public int x;
    public int y;
    public boolean fs;

    public Data(int x, int y, boolean fs) {
      this.x = x;
      this.y = y;
      this.fs = fs;
    }

    public void advance() {
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
  }
}
