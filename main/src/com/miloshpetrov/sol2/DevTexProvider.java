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
    myMissingTex = new Texture("imgSrcs/misc/missing.png");
  }

  @Override
  public TextureAtlas.AtlasRegion getTex(String name, FileHandle configFile) {
    FileHandle fh = SolFiles.readOnly(PREF + name + SUFF);
    return newTex(fh, name, configFile);
  }

  private TextureAtlas.AtlasRegion newTex(FileHandle fh, String name, FileHandle configFile) {
    Texture tex;
    if (fh.exists()) {
      tex = new Texture(fh);
    } else {
      tex = myMissingTex;
      DebugCollector.warn("texture not found:", fh);
    }
    String definedBy = configFile == null ? "hardcoded" : configFile.toString();
    return new SolTex(tex, name, definedBy);
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
      // todo check that the rest is number;
      res.add(newTex(fh, name, configFile));
    }
    return res;
  }

  @Override
  public TextureAtlas.AtlasRegion getCopy(TextureAtlas.AtlasRegion tex) {
    SolTex st = (SolTex) tex;
    return new SolTex(st.getTexture(), st.name, st.definedBy);
  }

  public static class SolTex extends TextureAtlas.AtlasRegion {
    public final String definedBy;

    public SolTex(Texture tex, String name, String definedBy) {
      super(tex, 0, 0, tex.getWidth(), tex.getHeight());
      this.definedBy = definedBy;
      flip(false, true);
      this.name = name;
    }
  }
}
