package org.destinationsol;

import com.badlogic.gdx.files.FileHandle;
import org.destinationsol.files.FileManager;

import java.util.*;

public class IniReader {

  private final HashMap<String,String> myVals;

  public IniReader(String fileName, SolFileReader reader, boolean readOnly) {
    myVals = new HashMap<String, String>();
    List<String> lines = reader != null ? reader.read(fileName) : fileToLines(fileName, readOnly);

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

  private List<String> fileToLines(String fileName, boolean readOnly) {
    FileManager.FileLocation accessType = readOnly ? FileManager.FileLocation.STATIC_FILES : FileManager.FileLocation.DYNAMIC_FILES;
    FileHandle fh = FileManager.getInstance().getFile(fileName, accessType);

    ArrayList<String> res = new ArrayList<String>();
    if (!fh.exists()) return res;
    for (String s : fh.readString().split("\n")) {
      res.add(s);
    }
    return res;
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
    FileHandle file = FileManager.getInstance().getDynamicFile(fileName);
    file.writeString(sb.toString(), false);
  }

}
