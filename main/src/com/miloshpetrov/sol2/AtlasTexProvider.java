package com.miloshpetrov.sol2;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;

class AtlasTexProvider implements TexProvider {
  private final TextureAtlas myAtlas;

  AtlasTexProvider(FileHandle atlasFile) {
    myAtlas = new TextureAtlas(atlasFile, true);
  }

  @Override
  public TextureAtlas.AtlasRegion getTex(String name, FileHandle configFile) {
    return myAtlas.findRegion(name);
  }

  @Override
  public void dispose() {
    myAtlas.dispose();
  }

  @Override
  public Sprite createSprite(String name) {
    return myAtlas.createSprite(name);
  }

  @Override
  public ArrayList<TextureAtlas.AtlasRegion> getTexs(String name, FileHandle configFile) {
    ArrayList<TextureAtlas.AtlasRegion> r = new ArrayList<TextureAtlas.AtlasRegion>();
    for (TextureAtlas.AtlasRegion rr : myAtlas.findRegions(name)) {
      r.add(rr);
    }
    return r;
  }

  @Override
  public TextureAtlas.AtlasRegion getCopy(TextureAtlas.AtlasRegion tex) {
    return new TextureAtlas.AtlasRegion(tex);
  }
}
