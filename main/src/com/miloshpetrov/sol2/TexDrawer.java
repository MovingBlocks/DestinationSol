package com.miloshpetrov.sol2;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface TexDrawer {
  void draw(TextureRegion tr, float width, float height, float origX, float origY, float x, float y, float rot,
    Color tint);
}
