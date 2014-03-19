package com.miloshpetrov.sol2;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.miloshpetrov.sol2.ui.DebugCollector;

import java.util.ArrayList;

public class DevTexProvider implements TexProvider {

  public static final String PREF = "imgSrcs/";
  public static final String SUFF = ".png";
  private final Texture myMissingTex;

  DevTexProvider() {
    FileHandle missingFile = SolFiles.readOnly("imgSrcs/misc/missing.png");
    myMissingTex = new Texture(missingFile);
  }

  @Override
  public TextureAtlas.AtlasRegion getTex(String fullName, FileHandle configFile) {
    FileHandle fh = SolFiles.readOnly(PREF + fullName + SUFF);
    return newTex(fh, fullName, -1, configFile);
  }

  private TextureAtlas.AtlasRegion newTex(FileHandle fh, String name, int idx, FileHandle configFile) {
    Texture tex;
    if (fh.exists()) {
      tex = new Texture(fh);
    } else {
      tex = myMissingTex;
      DebugCollector.warn("texture not found:", fh);
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
    Texture tex = new Texture(SolFiles.readOnly(PREF + name + SUFF));
    return new Sprite(tex);
  }

  @Override
  public ArrayList<TextureAtlas.AtlasRegion> getTexs(String name, FileHandle configFile) {
    FileHandle file = SolFiles.readOnly(PREF + name + SUFF);
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
