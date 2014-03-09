package com.miloshpetrov.sol2.game.maze;

public class MazeLayout {
  public final boolean[][] cells;
  public final boolean[][] right;
  public final boolean[][] down;

  public MazeLayout(boolean[][] cells, boolean[][] right, boolean[][] down) {
    this.cells = cells;
    this.right = right;
    this.down = down;
  }

}
