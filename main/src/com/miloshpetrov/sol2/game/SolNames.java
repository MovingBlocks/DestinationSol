package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.SolFiles;

import java.util.ArrayList;

public class SolNames {
  public final ArrayList<String> planets;
  public final ArrayList<String> systems;

  public SolNames() {
    planets = readList("planet");
    systems = readList("system");
  }

  private ArrayList<String> readList(String entityType) {
    ArrayList<String> list = new ArrayList<String>();
    FileHandle f = SolFiles.readOnly(Const.CONFIGS_DIR + entityType + "Names.txt");
    String lines = f.readString();
    for (String line : lines.split("\n")) {
      if (line.isEmpty()) continue;
      list.add(line);
    }
    return list;
  }
}
