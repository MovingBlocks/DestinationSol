package org.destinationsol.game.maze;

public class MazeLayout {
  public final boolean[][] inners;
  public final boolean[][] holes;
  public final boolean[][] right;
  public final boolean[][] down;

  public MazeLayout(boolean[][] inners, boolean[][] holes, boolean[][] right, boolean[][] down) {
    this.inners = inners;
    this.holes = holes;
    this.right = right;
    this.down = down;
  }

}
