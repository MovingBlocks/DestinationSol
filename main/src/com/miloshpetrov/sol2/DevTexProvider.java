package com.miloshpetrov.sol2;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.miloshpetrov.sol2.ui.DebugCollector;

class DevTexProvider implements TexProvider {

  public static final String PREF = "imgSrcs/";
  public static final String SUFF = ".png";
  private final Texture myMissingTex;

  DevTexProvider() {
    myMissingTex = new Texture("imgSrcs/misc/missing.png");
  }

  @Override
  public TextureAtlas.AtlasRegion getTex(String name) {
    FileHandle fh = SolFiles.readOnly(PREF + name + SUFF);
    return newTex(fh, name);
  }

  private TextureAtlas.AtlasRegion newTex(FileHandle fh, String name) {
    Texture tex;
    if (fh.exists()) {
      tex = new Texture(fh);
    } else {
      tex = myMissingTex;
      DebugCollector.warn("texture not found:", fh);
    }
    SolTex res = new SolTex(tex);
    res.flip(false, true);
    res.name = name;
    return res;
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
  public Array<TextureAtlas.AtlasRegion> getTexs(String name) {
    FileHandle file = SolFiles.readOnly(PREF + name + SUFF);
    FileHandle dir = file.parent();
    String baseName = file.nameWithoutExtension();
    Array<TextureAtlas.AtlasRegion> res = new Array<TextureAtlas.AtlasRegion>();
    for (FileHandle fh : dir.list()) {
      if (fh.isDirectory()) continue;
      String fhName = fh.nameWithoutExtension();
      String[] parts = fhName.split("_");
      if (parts.length != 2) continue;
      if (!parts[0].equals(baseName)) continue;
      // todo check that the rest is number;
      res.add(newTex(fh, name));
    }
    return res;
  }

  public static class SolTex extends TextureAtlas.AtlasRegion {
    public SolTex(Texture tex) {
      super(tex, 0, 0, tex.getWidth(), tex.getHeight());
    }
  }
}
