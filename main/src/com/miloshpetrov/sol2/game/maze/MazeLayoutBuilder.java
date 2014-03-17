package com.miloshpetrov.sol2.game.maze;

import com.miloshpetrov.sol2.common.SolMath;

public class MazeLayoutBuilder {
  public static final float HOLE_PERC = .2f;
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
    setInners();
    for (int i = 0; i < mySz; i++) {
      for (int j = 0; j < mySz; j++) {
        myRight[i][j] = SolMath.test(.5f);
        myDown[i][j] = SolMath.test(.5f);
      }
    }
    return new MazeLayout(myInners, myHoles, myRight, myDown);
  }

  private void setInners() {
    float[][] vals = new float[mySz][mySz];
    for (int i = 0; i < mySz; i++) {
      for (int j = 0; j < mySz; j++) {
        vals[i][j] = SolMath.rnd(0, 1);
      }
    }
    smooth(vals);
    smooth(vals);
    float min = 1;
    float max = 0;
    for (int i = 0; i < mySz; i++) {
      for (int j = 0; j < mySz; j++) {
        float v = vals[i][j];
        if (max < v) max = v;
        if (v < min) min = v;
      }
    }
    float mul = 1/(max - min);
    for (int i = 0; i < mySz; i++) {
      for (int j = 0; j < mySz; j++) {
        vals[i][j] = mul * (vals[i][j] - min);
      }
    }
    int v1 = 0;
    int v2 = 0;
    int v3 = 0;
    float c1 = HOLE_PERC - .05f;
    float c2 = HOLE_PERC;
    float c3 = HOLE_PERC + .05f;
    for (int i = 0; i < mySz; i++) {
      for (int j = 0; j < mySz; j++) {
        if (vals[i][j] < c1) v1++;
        if (vals[i][j] < c2) v2++;
        if (vals[i][j] < c3) v3++;
      }
    }
    float p1 = 1f * v1 / mySz / mySz;
    float p2 = 1f * v2 / mySz / mySz;
    float p3 = 1f * v3 / mySz / mySz;
    p1 = SolMath.abs(p1 - HOLE_PERC);
    p2 = SolMath.abs(p2 - HOLE_PERC);
    p3 = SolMath.abs(p3 - HOLE_PERC);
    float cutoff;
    if (p1 < p2) {
      cutoff = p1 < p3 ? c1 : c3;
    } else {
      cutoff = p2 < p3 ? c2 : c3;
    }
    for (int i = 0; i < mySz; i++) {
      for (int j = 0; j < mySz; j++) {
        if (isOk(i, j) && cutoff < vals[i][j]) myInners[i][j] = true;
      }
    }
  }

  private void smooth(float[][] vals) {
    for (int col = 0; col < mySz; col++) {
      for (int row = 0; row < mySz; row++) {
        float v = vals[col][row];
        float lv = col == 0 ? 1 : vals[col-1][row];
        float rv = col == mySz-1 ? 1 : vals[col+1][row];
        float uv = row == 0 ? 1 : vals[col][row-1];
        float dv = row == mySz-1 ? 1 : vals[col][row+1];
        vals[col][row] = (v + lv + rv + uv + dv) / 5;
      }
    }
  }

  private boolean isOk(int i, int j) {
    int ii = i - mySz / 2;
    int jj = j - mySz / 2;
    float dist = SolMath.sqrt(ii * ii + jj * jj);
    return dist < mySz/2;
  }
}
