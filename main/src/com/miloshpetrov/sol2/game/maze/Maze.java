package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.game.HardnessCalc;
import com.miloshpetrov.sol2.game.SolGame;

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
    Vector2 camPos = game.getCam().getPos();
    if (!myObjsCreated && camPos.dst(myPos) < myRadius) {
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
