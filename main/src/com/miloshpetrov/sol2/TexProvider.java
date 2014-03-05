package com.miloshpetrov.sol2;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

interface TexProvider {
  TextureAtlas.AtlasRegion getTex(String name);
  void dispose();
  Sprite createSprite(String name);
  Array<TextureAtlas.AtlasRegion> getTexs(String name);
}
