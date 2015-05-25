package com.miloshpetrov.sol2;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;

interface TextureProvider {
  TextureAtlas.AtlasRegion getTex(String fullName, FileHandle configFile);
  void dispose();
  Sprite createSprite(String name);
  ArrayList<TextureAtlas.AtlasRegion> getTexs(String name, FileHandle configFile);
  TextureAtlas.AtlasRegion getCopy(TextureAtlas.AtlasRegion tex);
}
