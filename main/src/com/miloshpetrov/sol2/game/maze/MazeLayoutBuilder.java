package com.miloshpetrov.sol2.game.maze;

import com.miloshpetrov.sol2.common.SolMath;

public class MazeLayoutBuilder {
  private final int mySz;
  private final boolean[][] myInners;
  private final boolean[][] myHoles;
  private final boolean[][] myRight;
  private final boolean[][] myDown;

  public MazeLayoutBuilder(int sz) {
    mySz = sz;
    myInners = new boolean[mySz][mySz];
    myHoles = new boolean[mySz][mySz];
    myRight = new boolean[mySz][mySz];
    myDown = new boolean[mySz][mySz];
  }

  public MazeLayout build() {
    for (int i = 0; i < mySz; i++) {
      for (int j = 0; j < mySz; j++) {
        myInners[i][j] = isOk(i, j) && SolMath.test(.5f);
        myRight[i][j] = SolMath.test(.5f);
        myDown[i][j] = SolMath.test(.5f);
      }
    }
    return new MazeLayout(myInners, myHoles, myRight, myDown);
  }

  private boolean isOk(int i, int j) {
    int ii = i - mySz / 2;
    int jj = j - mySz / 2;
    float dist = SolMath.sqrt(ii * ii + jj * jj);
    return dist < mySz/2;
  }
}
