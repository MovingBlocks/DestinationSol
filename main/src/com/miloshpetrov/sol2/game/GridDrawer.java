package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.TexMan;
import com.miloshpetrov.sol2.common.Col;

public class GridDrawer {

  private final TextureAtlas.AtlasRegion myLineTex;

  public GridDrawer(TexMan texMan) {
    myLineTex = texMan.getTex("mapObjs/gridLine", null);
  }

  public void draw(Drawer drawer, SolGame game, float gridSz) {
    SolCam cam = game.getCam();
    float lw = 3*cam.getRealLineWidth();
    Vector2 camPos = cam.getPos();
    float viewDist = cam.getViewDist(cam.getRealZoom());
    float x = (int) ((camPos.x - viewDist) / gridSz) * gridSz;
    float y = (int) ((camPos.y - viewDist) / gridSz) * gridSz;
    int count = (int)(viewDist * 2 / gridSz);
    Color col = Col.UI_INACTIVE;
    for (int i = 0; i < count; i++) {
      drawer.draw(myLineTex, viewDist * 2, lw, 0, lw/2, x, y, 0, col);
      drawer.draw(myLineTex, viewDist * 2, lw, 0, lw/2, x, y, 90, col);
      drawer.draw(myLineTex, viewDist * 2, lw, 0, lw/2, x, y, 180, col);
      drawer.draw(myLineTex, viewDist * 2, lw, 0, lw/2, x, y, -90, col);
      x += gridSz;
      y += gridSz;
    }
  }
}
