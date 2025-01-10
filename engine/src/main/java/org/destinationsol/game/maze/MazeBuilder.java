/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.destinationsol.game.maze;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;
import org.destinationsol.game.ShipConfig;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.input.AiPilot;
import org.destinationsol.game.input.Pilot;
import org.destinationsol.game.input.StillGuard;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.ShipBuilder;

import java.util.ArrayList;

public class MazeBuilder {
    public static final float BORDER = 4f;
    public static final float TILE_SZ = 3.5f;
    private int size;
    private Vector2 mazePosition;
    private float mazeAngle;
    private float innerRadius;

    public void build(SolGame game, Maze maze) {
        innerRadius = maze.getRadius() - BORDER;
        size = (int) (innerRadius * 2 / TILE_SZ);
        mazePosition = maze.getPos();
        mazeAngle = SolRandom.seededRandomFloat(180);
        MazeLayout mazeLayout = buildMaze(game, maze);
        buildEnemies(game, maze, mazeLayout);
    }

    private MazeLayout buildMaze(SolGame game, Maze maze) {
        MazeLayout layout;
        if (maze.hasLayoutSet()) {
            layout = new MazeLayoutBuilder(size).build(maze.getMazeLayout().inners);
        } else {
            layout = new MazeLayoutBuilder(size).build();
        }

        new MazeTileObject.Builder();
        MazeConfig config = maze.getConfig();
        for (int col = 0; col < size; col++) {
            for (int row = 0; row < size; row++) {
                boolean ulInner = col > 0 && row > 0 && layout.inners[col][row];
                boolean rInner = row > 0 && col < size - 1 && layout.inners[col + 1][row];
                if (row > 0 && (ulInner || rInner)) {
                    boolean wall = layout.right[col][row];
                    boolean inner = ulInner && rInner;
                    float tileAngle = mazeAngle - 90;
                    if (!ulInner) {
                        tileAngle += 180;
                    }
                    Vector2 tilePos = cellPos(col, row, TILE_SZ / 2, 0f);
                    ArrayList<MazeTile> tiles;
                    if (wall) {
                        tiles = inner ? config.innerWalls : config.borderWalls;
                    } else {
                        tiles = inner ? config.innerPasses : config.borderPasses;
                    }
                    MazeTile tile = SolRandom.seededRandomElement(tiles);
                    MazeTileObject.MyFar mto = new MazeTileObject.MyFar(tile, tileAngle, new Vector2(tilePos), SolRandom.test(.5f));
                    game.getObjectManager().addFarObjNow(mto);
                }

                boolean dInner = col > 0 && row < size - 1 && layout.inners[col][row + 1];
                if (col > 0 && (ulInner || dInner)) {
                    boolean wall = layout.down[col][row];
                    boolean inner = ulInner && dInner;
                    float tileAngle = mazeAngle;
                    if (!ulInner) {
                        tileAngle += 180;
                    }
                    Vector2 tilePos = cellPos(col, row, 0f, TILE_SZ / 2);
                    ArrayList<MazeTile> tiles;
                    if (wall) {
                        tiles = inner ? config.innerWalls : config.borderWalls;
                    } else {
                        tiles = inner ? config.innerPasses : config.borderPasses;
                    }
                    MazeTile tile = SolRandom.seededRandomElement(tiles);
                    MazeTileObject.MyFar mto = new MazeTileObject.MyFar(tile, tileAngle, new Vector2(tilePos), SolRandom.test(.5f));
                    game.getObjectManager().addFarObjNow(mto);
                }
            }
        }
        return layout;
    }

    private Vector2 cellPos(int col, int row, float xOffset, float yOffset) {
        Vector2 res = new Vector2((col - size / 2) * TILE_SZ + xOffset, (row - size / 2) * TILE_SZ + yOffset);
        SolMath.rotate(res, mazeAngle);
        res.add(mazePosition);
        return res;
    }

    private void buildEnemies(SolGame game, Maze maze, MazeLayout layout) {
        MazeConfig config = maze.getConfig();
        float dist = maze.getRadius() - BORDER / 2;
        float circleLen = dist * MathUtils.PI * 2;
        for (ShipConfig enemy : config.outerEnemies) {
            int count = (int) (enemy.density * circleLen);
            for (int i = 0; i < count; i++) {
                Vector2 position = new Vector2();
                SolMath.fromAl(position, SolRandom.randomFloat(180), dist);
                position.add(mazePosition);
                buildEnemy(position, game, enemy, false);
            }
        }

        boolean[][] occupiedCells = new boolean[size][size];
        occupiedCells[size / 2][size / 2] = true;
        for (ShipConfig e : config.innerEnemies) {
            int count = (int) (e.density * innerRadius * innerRadius * MathUtils.PI);
            for (int i = 0; i < count; i++) {
                Vector2 position = getFreeCellPos(occupiedCells);
                if (position != null) {
                    buildEnemy(position, game, e, true);
                }
            }
        }
        ShipConfig bossConfig = SolRandom.randomElement(config.bosses);
        Vector2 position = cellPos(size / 2, size / 2, 0f, 0f);
        buildEnemy(position, game, bossConfig, true);
    }

    private Vector2 getFreeCellPos(boolean[][] occupiedCells) {
        for (int i = 0; i < 10; i++) {
            int col = SolRandom.seededRandomInt(size);
            int row = SolRandom.seededRandomInt(size);
            if (occupiedCells[col][row]) {
                continue;
            }
            Vector2 position = cellPos(col, row, 0f, 0f);
            if (.8f * innerRadius < position.dst(mazePosition)) {
                continue;
            }
            occupiedCells[col][row] = true;
            return position;
        }
        return null;
    }

    private void buildEnemy(Vector2 position, SolGame game, ShipConfig e, boolean inner) {
        float angle = SolRandom.randomFloat(180);
        ShipBuilder sb = game.getShipBuilder();
        float viewDist = Const.AI_DET_DIST;
        if (inner) {
            viewDist = TILE_SZ * 1.25f;
        }
        Pilot pilot = new AiPilot(new StillGuard(position, game, e), false, game.getFactionMan().getBuilderForHull(e.hull), true, null, viewDist);
        int money = e.money;
        FarShip s = sb.buildNewFar(game, position, new Vector2(), angle, 0, pilot, e.items, e.hull, null, false, money, null, true);
        game.getObjectManager().addFarObjNow(s);
    }

}
