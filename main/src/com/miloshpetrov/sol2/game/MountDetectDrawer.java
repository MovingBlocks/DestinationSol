/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
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
