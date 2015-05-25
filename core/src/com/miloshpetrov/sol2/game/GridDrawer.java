package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.TextureManager;
import com.miloshpetrov.sol2.common.SolColor;

public class GridDrawer {

  public GridDrawer(TextureManager textureManager) {
  }

  public void draw(GameDrawer drawer, SolGame game, float gridSz, TextureAtlas.AtlasRegion tex) {
    SolCam cam = game.getCam();
    float lw = 4 * cam.getRealLineWidth();
    Vector2 camPos = cam.getPos();
    float viewDist = cam.getViewDist(cam.getRealZoom());
    float x = (int) ((camPos.x - viewDist) / gridSz) * gridSz;
    float y = (int) ((camPos.y - viewDist) / gridSz) * gridSz;
    int count = (int)(viewDist * 2 / gridSz);
    Color col = SolColor.UI_INACTIVE;
    for (int i = 0; i < count; i++) {
      drawer.draw(tex, lw, viewDist * 2, lw/2, 0, x, y, 0, col);
      drawer.draw(tex, lw, viewDist * 2, lw/2, 0, x, y, 90, col);
      drawer.draw(tex, lw, viewDist * 2, lw/2, 0, x, y, 180, col);
      drawer.draw(tex, lw, viewDist * 2, lw/2, 0, x, y, -90, col);
      x += gridSz;
      y += gridSz;
    }
  }
}
