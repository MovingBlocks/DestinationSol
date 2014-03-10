package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.SolGame;

import java.util.ArrayList;

public class MazeBuilder {
  public static final float BORDER = 4f;
  public static final float TILE_SZ = 4f;

  public void build(SolGame game, Maze maze) {
    buildMaze(game, maze);
    buildEnemies(game, maze);
  }

  public void buildMaze(SolGame game, Maze maze) {
    float rad = maze.getRadius() - BORDER;
    Vector2 mazePos = maze.getPos();
    int sz = (int) (rad * 2 / TILE_SZ);
    float mazeAngle = 0;SolMath.rnd(180);
    MazeLayout layout = new MazeLayoutBuilder(sz).build();
    MazeTileObj.Builder builder = new MazeTileObj.Builder();
    MazeConfig config = maze.getConfig();
    for (int col = 0; col < sz; col++) {
      for (int row = 0; row < sz; row++) {
        boolean ulInner = col > 0 && row > 0 && layout.cells[col][row];
        boolean rInner = row > 0 && col < sz - 1 && layout.cells[col + 1][row];
        if (row > 0 && (ulInner || rInner)) {
          boolean wall = layout.right[col][row];
          boolean inner = ulInner && rInner;
          float tileAngle = mazeAngle - 90;
          if (!ulInner) tileAngle += 180;
          Vector2 tilePos = new Vector2((col - sz/2) * TILE_SZ + TILE_SZ/2, (row - sz/2) * TILE_SZ);
          SolMath.rotate(tilePos, mazeAngle);
          tilePos.add(mazePos);
          ArrayList<MazeTile> tiles;
          if (wall) {
            tiles = inner ? config.innerWalls : config.borderWalls;
          } else {
            tiles = inner ? config.innerPasses : config.borderPasses;
          }
          MazeTile tile = SolMath.elemRnd(tiles);
          MazeTileObj mto = builder.build(game, tile, tilePos, tileAngle);
          game.getObjMan().addObjDelayed(mto);
        }

        boolean dInner = col > 0 && row < sz - 1 && layout.cells[col][row + 1];
        if (col > 0 && (ulInner || dInner)) {
          boolean wall = layout.down[col][row];
          boolean inner = ulInner && dInner;
          float tileAngle = mazeAngle;
          if (!ulInner) tileAngle += 180;
          Vector2 tilePos = new Vector2((col - sz/2) * TILE_SZ, (row - sz/2) * TILE_SZ + TILE_SZ/2);
          SolMath.rotate(tilePos, mazeAngle);
          tilePos.add(mazePos);
          ArrayList<MazeTile> tiles;
          if (wall) {
            tiles = inner ? config.innerWalls : config.borderWalls;
          } else {
            tiles = inner ? config.innerPasses : config.borderPasses;
          }
          MazeTile tile = SolMath.elemRnd(tiles);
          MazeTileObj mto = builder.build(game, tile, tilePos, tileAngle);
          game.getObjMan().addObjDelayed(mto);
        }
      }
    }
  }

  private void buildEnemies(SolGame game, Maze maze) {
    MazeConfig config = maze.getConfig();
//    config.outerEnemies
  }


}
