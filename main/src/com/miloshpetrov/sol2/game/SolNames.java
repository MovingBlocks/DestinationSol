/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.files.FileHandle;
import com.miloshpetrov.sol2.files.FileManager;

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
    FileHandle f = FileManager.getInstance().getConfigDirectory().child(entityType + "Names.txt");
    String lines = f.readString();
    for (String line : lines.split("\n")) {
      if (line.isEmpty()) continue;
      list.add(line);
    }
    return list;
  }
}
