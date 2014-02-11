package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.Col;

public class GridDrawer {

  public void draw(Drawer drawer, SolGame game, float gridSz) {
    SolCam cam = game.getCam();
    float lw = cam.getRealLineWidth();
    Vector2 camPos = cam.getPos();
    float viewDist = cam.getViewDist(cam.getRealZoom());
    float x = (int) ((camPos.x - viewDist) / gridSz) * gridSz;
    float y = (int) ((camPos.y - viewDist) / gridSz) * gridSz;
    int count = (int)(viewDist * 2 / gridSz);
    Color col = Col.W15;
    for (int i = 0; i < count; i++) {
      drawer.drawLine(x, y, 0, viewDist * 2, col, lw);
      drawer.drawLine(x, y, 90, viewDist * 2, col, lw);
      drawer.drawLine(x, y, 180, viewDist * 2, col, lw);
      drawer.drawLine(x, y, -90, viewDist * 2, col, lw);
      x += gridSz;
      y += gridSz;
    }
  }
}
