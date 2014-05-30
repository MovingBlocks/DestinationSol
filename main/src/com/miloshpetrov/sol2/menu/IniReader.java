package com.miloshpetrov.sol2.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.miloshpetrov.sol2.SolFiles;
import com.miloshpetrov.sol2.game.DebugOptions;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class IniReader {

  private final HashMap<String,String> myVals;

  public IniReader(String fileName, boolean handlersReady) {
    myVals = new HashMap<String, String>();
    if (DebugOptions.DEV_ROOT_PATH != null) fileName = DebugOptions.DEV_ROOT_PATH + fileName;
    List<String> lines = handlersReady ? fileToLines2(fileName) : fileToLines(fileName);

    for (String line : lines) {
      int commentStart = line.indexOf('#');
      if (commentStart >= 0) {
        line = line.substring(0, commentStart);
      }
      String[] sides = line.split("=");
      if (sides.length < 2) continue;
      String key = sides[0].trim();
      String val = sides[1].trim();
      myVals.put(key, val);
    }
  }

  private List<String> fileToLines2(String fileName) {
    FileHandle fh = SolFiles.readOnly(fileName);
    ArrayList<String> res = new ArrayList<String>();
    if (!fh.exists()) return res;
    for (String s : fh.readString().split("\n")) {
      res.add(s);
    }
    return res;
  }

  private List<String> fileToLines(String fileName) {
    ArrayList<String> lines = new ArrayList<String>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      String line = "";
      while ((line = br.readLine()) != null) {
        lines.add(line);
      }
      br.close();
    } catch (IOException ignore) {
    }
    return lines;
  }

  private String fileToLines0(String fileName) {
    String lines = "";
    try {
      byte[] encoded = Files.readAllBytes(Paths.get(fileName));
      lines = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString();
    } catch (IOException ignore) {
    }
    return lines;
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
