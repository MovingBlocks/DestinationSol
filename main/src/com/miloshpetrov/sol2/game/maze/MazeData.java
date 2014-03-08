package com.miloshpetrov.sol2.game.maze;

public class MazeData {
  public final boolean[][] cells;
  public final boolean[][] right;
  public final boolean[][] down;

  public MazeData(boolean[][] cells, boolean[][] right, boolean[][] down) {
    this.cells = cells;
    this.right = right;
    this.down = down;
  }

}
