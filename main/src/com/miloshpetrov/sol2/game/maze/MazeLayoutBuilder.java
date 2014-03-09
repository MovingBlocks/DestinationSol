package com.miloshpetrov.sol2.game.maze;

import com.miloshpetrov.sol2.common.SolMath;

public class MazeLayoutBuilder {
  private final int mySz;
  private final boolean[][] myCells;
  private final boolean[][] myRight;
  private final boolean[][] myDown;

  public MazeLayoutBuilder(int sz) {
    mySz = sz;
    myCells = new boolean[mySz][mySz];
    myRight = new boolean[mySz][mySz];
    myDown = new boolean[mySz][mySz];
  }

  public MazeLayout build() {
    for (int i = 0; i < mySz; i++) {
      for (int j = 0; j < mySz; j++) {
        myCells[i][j] = SolMath.test(.5f);
        myRight[i][j] = SolMath.test(.5f);
        myDown[i][j] = SolMath.test(.5f);
      }
    }
    return new MazeLayout(myCells, myRight, myDown);
  }
}
