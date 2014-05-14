package com.miloshpetrov.sol2.game.maze;

import com.badlogic.gdx.math.Vector2;
import com.miloshpetrov.sol2.Const;
import com.miloshpetrov.sol2.common.SolMath;
import com.miloshpetrov.sol2.game.*;
import com.miloshpetrov.sol2.game.input.*;
import com.miloshpetrov.sol2.game.ship.ShipBuilder;
import com.miloshpetrov.sol2.game.ship.SolShip;

import java.util.ArrayList;

public class MazeBuilder {
  public static final float BORDER = 4f;
  public static final float TILE_SZ = 3f;
  private int mySz;
  private Vector2 myMazePos;
  private float myMazeAngle;
  private float myInnerRad;

  public void build(SolGame game, Maze maze) {
    myInnerRad = maze.getRadius() - BORDER;
    mySz = (int) (myInnerRad * 2 / TILE_SZ);
    myMazePos = maze.getPos();
    myMazeAngle = SolMath.rnd(180);

    MazeLayout layout = buildMaze(game, maze);
    buildEnemies(game, maze, layout);
  }

  public MazeLayout buildMaze(SolGame game, Maze maze) {
    MazeLayout layout = new MazeLayoutBuilder(mySz).build();
    MazeTileObj.Builder builder = new MazeTileObj.Builder();
    MazeConfig config = maze.getConfig();
    for (int col = 0; col < mySz; col++) {
      for (int row = 0; row < mySz; row++) {
        boolean ulInner = col > 0 && row > 0 && layout.inners[col][row];
        boolean rInner = row > 0 && col < mySz - 1 && layout.inners[col + 1][row];
        if (row > 0 && (ulInner || rInner)) {
          boolean wall = layout.right[col][row];
          boolean inner = ulInner && rInner;
          float tileAngle = myMazeAngle - 90;
          if (!ulInner) tileAngle += 180;
          Vector2 tilePos = cellPos(col, row, TILE_SZ / 2, 0f);
          ArrayList<MazeTile> tiles;
          if (wall) {
            tiles = inner ? config.innerWalls : config.borderWalls;
          } else {
            tiles = inner ? config.innerPasses : config.borderPasses;
          }
          MazeTile tile = SolMath.elemRnd(tiles);
          MazeTileObj mto = builder.build(game, tile, tilePos, tileAngle, SolMath.test(.5f));
          game.getObjMan().addObjDelayed(mto);
        }

        boolean dInner = col > 0 && row < mySz - 1 && layout.inners[col][row + 1];
        if (col > 0 && (ulInner || dInner)) {
          boolean wall = layout.down[col][row];
          boolean inner = ulInner && dInner;
          float tileAngle = myMazeAngle;
          if (!ulInner) tileAngle += 180;
          Vector2 tilePos = cellPos(col, row, 0f, TILE_SZ / 2);
          ArrayList<MazeTile> tiles;
          if (wall) {
            tiles = inner ? config.innerWalls : config.borderWalls;
          } else {
            tiles = inner ? config.innerPasses : config.borderPasses;
          }
          MazeTile tile = SolMath.elemRnd(tiles);
          MazeTileObj mto = builder.build(game, tile, tilePos, tileAngle, SolMath.test(.5f));
          game.getObjMan().addObjDelayed(mto);
        }
      }
    }
    return layout;
  }

  private Vector2 cellPos(int col, int row, float xOffset, float yOffset) {
    Vector2 res = new Vector2((col - mySz / 2) * TILE_SZ + xOffset, (row - mySz / 2) * TILE_SZ + yOffset);
    SolMath.rotate(res, myMazeAngle);
    res.add(myMazePos);
    return res;
  }

  private void buildEnemies(SolGame game, Maze maze, MazeLayout layout) {
    MazeConfig config = maze.getConfig();
    float dist = maze.getRadius() - BORDER / 2;
    float circleLen = dist * SolMath.PI * 2;
    for (ShipConfig e : config.outerEnemies) {
      int count = (int) (e.density * circleLen);
      for (int i = 0; i < count; i++) {
        Vector2 pos = new Vector2();
        SolMath.fromAl(pos, SolMath.rnd(180), dist);
        pos.add(myMazePos);
        buildEnemy(pos, game, e, false);
      }
    }

    boolean[][] occupiedCells = new boolean[mySz][mySz];
    occupiedCells[mySz/2][mySz/2] = true;
    for (ShipConfig e : config.innerEnemies) {
      int count = (int) (e.density * myInnerRad * myInnerRad * SolMath.PI);
      for (int i = 0; i < count; i++) {
        Vector2 pos = getFreeCellPos(occupiedCells);
        if (pos != null) buildEnemy(pos, game, e, true);
      }
    }
    ShipConfig bossConfig = SolMath.elemRnd(config.bosses);
    Vector2 pos = cellPos(mySz / 2, mySz / 2, 0f, 0f);
    buildEnemy(pos, game, bossConfig, true);
  }

  private Vector2 getFreeCellPos(boolean[][] occupiedCells) {
    for (int i = 0; i < 10; i++) {
      int col = SolMath.intRnd(mySz);
      int row = SolMath.intRnd(mySz);
      if (occupiedCells[col][row]) continue;
      Vector2 pos = cellPos(col, row, 0f, 0f);
      if (.8f * myInnerRad < pos.dst(myMazePos)) continue;
      occupiedCells[col][row] = true;
      return pos;
    }
    return null;
  }

  private void buildEnemy(Vector2 pos, SolGame game, ShipConfig e, boolean inner) {
    float angle = SolMath.rnd(180);
    ShipBuilder sb = game.getShipBuilder();
    float viewDist = Const.AI_DET_DIST;
    if (inner) viewDist *= .2f;
    Pilot pilot = new AiPilot(new StillGuard(pos, game, e), false, Fraction.EHAR, true, null, viewDist);
    boolean hasRepairer;
    hasRepairer = e.hasRepairer;
    int money = e.money;
    SolShip s = sb.buildNew(game, pos, new Vector2(), angle, 0, pilot, e.items, e.hull, null, hasRepairer, money, null);
    game.getObjMan().addObjDelayed(s);
  }


}
