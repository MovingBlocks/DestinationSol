package org.destinationsol;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;

class AtlasTextureProvider implements TextureProvider {
  private final TextureAtlas myAtlas;

  AtlasTextureProvider(FileHandle atlasFile) {
    myAtlas = new TextureAtlas(atlasFile, true);
  }

  @Override
  public TextureAtlas.AtlasRegion getTex(String fullName, FileHandle configFile) {
    return myAtlas.findRegion(fullName);
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
