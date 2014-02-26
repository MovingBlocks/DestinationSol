package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.miloshpetrov.sol2.game.DebugAspects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class IniReader {

  private final HashMap<String,String> myVals;

  public IniReader(String fileName) {
    myVals = new HashMap<String, String>();
    String lines = "";
    if (DebugAspects.DEV_ROOT_PATH != null) fileName = DebugAspects.DEV_ROOT_PATH + fileName;
    try {
      byte[] encoded = Files.readAllBytes(Paths.get(fileName));
      lines = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
    } catch (IOException ignore) {
    }

    for (String line : lines.split("\n")) {
      String[] sides = line.split("=");
      if (sides.length < 2) continue;
      String key = sides[0].trim();
      String val = sides[1].trim();
      myVals.put(key, val);
    }
  }

  public String s(String key, String def) {
    String st = myVals.get(key);
    return st == null ? def : st;
  }

  public int i(String key, int def) {
    String st = myVals.get(key);
    return st == null ? def : Integer.parseInt(st);
  }

  public boolean b(String key, boolean def) {
    String st = myVals.get(key);
    return st == null ? def : "true".equalsIgnoreCase(st);
  }

  public float f(String key, float def) {
    String st = myVals.get(key);
    return st == null ? def : Float.parseFloat(st);
  }

  public static void write(String fileName, Object ... keysVals) {
    boolean second = false;
    StringBuilder sb = new StringBuilder();
    for (Object o : keysVals) {
      String s = o.toString();
      sb.append(s);
      sb.append(second ? '\n' : '=');
      second = !second;
    }
    FileHandle file = Gdx.files.local(fileName);
    file.writeString(sb.toString(), false);
  }
}
