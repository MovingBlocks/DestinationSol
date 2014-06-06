package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.game.*;

public class Maze {

  private final MazeConfig myConfig;
  private final Vector2 myPos;
  private final float myRadius;
  private final float myDps;
  private boolean myObjsCreated;

  public Maze(MazeConfig config, Vector2 pos, float radius) {
    myConfig = config;
    myPos = pos;
    myRadius = radius;
    myDps = HardnessCalc.getMazeDps(config);
  }

  public void update(SolGame game) {
    SolCam cam = game.getCam();
    Vector2 camPos = cam.getPos();
    if (!myObjsCreated && camPos.dst(myPos) < myRadius + Const.CAM_VIEW_DIST_JOURNEY * 2) {
      new MazeBuilder().build(game, this);
      myObjsCreated = true;
    }
  }

  public MazeConfig getConfig() {
    return myConfig;
  }

  public Vector2 getPos() {
    return myPos;
  }

  /**
   * @return the full radius including the exterior border.
   */
  public float getRadius() {
    return myRadius;
  }

  public float getDps() {
    return myDps;
  }
}
