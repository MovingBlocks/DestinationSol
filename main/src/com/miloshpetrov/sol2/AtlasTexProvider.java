package com.miloshpetrov.sol2;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

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
  public Array<TextureAtlas.AtlasRegion> getTexs(String name, FileHandle configFile) {
    return myAtlas.findRegions(name);
  }

  @Override
  public TextureAtlas.AtlasRegion getCopy(TextureAtlas.AtlasRegion tex) {
    return new TextureAtlas.AtlasRegion(tex);
  }
}
