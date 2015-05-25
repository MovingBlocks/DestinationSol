package com.miloshpetrov.sol2.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.TextureManager;
import com.miloshpetrov.sol2.common.SolColor;
import com.miloshpetrov.sol2.game.ship.SolShip;

public class MountDetectDrawer {
  private final Vector2 myNePos;
  private final TextureAtlas.AtlasRegion myTex;

  private boolean myShouldDraw;
  private float myBaseRad;
  private float myAnimPerc;
  private float myAngle;

  public MountDetectDrawer(TextureManager textureManager) {
    myNePos = new Vector2();
    myTex = textureManager.getTex("smallGameObjs/targetDetected", null);
  }

  public void update(SolGame game) {
    myShouldDraw = false;
    float ts = game.getTimeStep();
    myAnimPerc += ts / 2f;
    if (myAnimPerc > 1) myAnimPerc = 0;
    myAngle += 30f * ts;
    if (myAngle > 180) myAngle -= 360;
  }

  public void setNe(SolShip ne) {
    myNePos.set(ne.getPos());
    myBaseRad = ne.getHull().config.approxRadius;
    myShouldDraw = true;
  }

  public void draw(GameDrawer drawer) {
    if (!myShouldDraw) return;
    float radPerc = myAnimPerc * 2;
    if (radPerc > 1) radPerc = 2 - radPerc;
    float rad = myBaseRad * (1 + .5f * radPerc);
    drawer.draw(myTex, rad * 2, rad * 2, rad, rad, myNePos.x, myNePos.y, myAngle, SolColor.W);
  }
}
