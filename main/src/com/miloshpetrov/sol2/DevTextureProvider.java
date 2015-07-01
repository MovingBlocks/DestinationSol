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
 
 package com.miloshpetrov.sol2;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.files.FileManager;
import com.miloshpetrov.sol2.game.DebugOptions;

import java.util.ArrayList;

public class DevTextureProvider implements TextureProvider {

  public static final String PREF = "imgSrcs/";
  public static final String SUFF = ".png";
  private final Texture myMissingTex;

  DevTextureProvider() {
    FileHandle missingFile = FileManager.getInstance().getStaticFile("imgSrcs/smallGameObjs/missing.png");
    myMissingTex = new Texture(missingFile);
  }

  @Override
  public TextureAtlas.AtlasRegion getTex(String fullName, FileHandle configFile) {
    FileHandle fh = FileManager.getInstance().getStaticFile(PREF + fullName + SUFF);
    return newTex(fh, fullName, -1, configFile);
  }

  private TextureAtlas.AtlasRegion newTex(FileHandle fh, String name, int idx, FileHandle configFile) {
    Texture tex;
    if (fh.exists()) {
      tex = new Texture(fh);
    } else {
      tex = myMissingTex;
      String msg = "texture not found: " + fh;
      DebugOptions.MISSING_TEXTURE_ACTION.handle(msg);
    }
    String definedBy = configFile == null ? "hardcoded" : configFile.toString();
    return new SolTex(tex, name, idx, definedBy);
  }

  @Override
  public void dispose() {
    // forget it
  }

  @Override
  public Sprite createSprite(String name) {
    Texture tex = new Texture(FileManager.getInstance().getStaticFile(PREF + name + SUFF));
    return new Sprite(tex);
  }

  @Override
  public ArrayList<TextureAtlas.AtlasRegion> getTexs(String name, FileHandle configFile) {
    FileHandle file = FileManager.getInstance().getStaticFile(PREF + name + SUFF);
    FileHandle dir = file.parent();
    String baseName = file.nameWithoutExtension();
    ArrayList<TextureAtlas.AtlasRegion> res = new ArrayList<TextureAtlas.AtlasRegion>();
    for (FileHandle fh : dir.list()) {
      if (fh.isDirectory()) continue;
      String fhName = fh.nameWithoutExtension();
      String[] parts = fhName.split("_");
      if (parts.length != 2) continue;
      if (!parts[0].equals(baseName)) continue;
      int idx = Integer.parseInt(parts[1]);
      res.add(newTex(fh, name, idx, configFile));
    }
    return res;
  }

  @Override
  public TextureAtlas.AtlasRegion getCopy(TextureAtlas.AtlasRegion tex) {
    SolTex st = (SolTex) tex;
    return new SolTex(st.getTexture(), st.name, st.index, st.definedBy);
  }

  public static class SolTex extends TextureAtlas.AtlasRegion {
    public final String definedBy;

    public SolTex(Texture tex, String name, int idx, String definedBy) {
      super(tex, 0, 0, tex.getWidth(), tex.getHeight());
      this.index = idx;
      this.definedBy = definedBy;
      flip(false, true);
      this.name = name;
    }
  }
}
