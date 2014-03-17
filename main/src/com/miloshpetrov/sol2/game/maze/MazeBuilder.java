package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.input.*;
import com.miloshpetrov.sol2.game.ship.ShipBuilder;
import com.miloshpetrov.sol2.game.ship.SolShip;

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
    int sz = (int) (rad * 2 / TILE_SZ);
    Vector2 mazePos = maze.getPos();
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
          Vector2 tilePos = cellCenter(sz, col, row);
          tilePos.x += TILE_SZ/2;
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
          Vector2 tilePos = cellCenter(sz, col, row);
          tilePos.y += TILE_SZ/2;
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

  private Vector2 cellCenter(int sz, int col, int row) {
    return new Vector2((col - sz/2) * TILE_SZ, (row - sz/2) * TILE_SZ);
  }

  private void buildEnemies(SolGame game, Maze maze) {
    MazeConfig config = maze.getConfig();
    float dist = maze.getRadius() - BORDER / 2;
    float circleLen = dist * SolMath.PI * 2;
    for (ShipConfig e : config.outerEnemies) {
      int count = (int) (e.density * circleLen);
      for (int i = 0; i < count; i++) {
        Vector2 pos = new Vector2();
        SolMath.fromAl(pos, SolMath.rnd(180), dist);
        pos.add(maze.getPos());
        buildEnemy(pos, game, e);
      }
    }

  }

  private void buildEnemy(Vector2 pos, SolGame game, ShipConfig e) {
    float angle = SolMath.rnd(180);
    ShipBuilder sb = game.getShipBuilder();
    Pilot pilot = new AiPilot(new NoDestProvider(), false, Fraction.EHAR, true, null, game.getCam().getSpaceViewDist());
    SolShip s = sb.buildNew(game, pos, new Vector2(), angle, 0, pilot, e.items, e.hull, true, true, null, false, 20, null);
    game.getObjMan().addObjDelayed(s);
  }


}
