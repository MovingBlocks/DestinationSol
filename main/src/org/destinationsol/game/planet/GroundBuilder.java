/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.destinationsol.game.planet;

import org.destinationsol.common.SolMath;

public class GroundBuilder {
    private static final int PURE_GROUND_ROWS = 0;

    private final PlanetConfig myConfig;
    private final int myCols;
    private final int myRows;
    private final boolean[][] myDungeon;
    private final Tile[][] myMap;

    public GroundBuilder(PlanetConfig planetConfig, int cols, int rows) {
        myConfig = planetConfig;
        myCols = cols;
        myRows = rows;
        myDungeon = new boolean[cols][rows];
        myMap = new Tile[cols][rows];
    }

    public Tile[][] build() {
        float[] ds0 = new float[myCols];
        float desiredMin = 0;
        float desiredMax = myRows - PURE_GROUND_ROWS;

        for (int x = 0; x < myCols; x++) {
            ds0[x] = SolMath.rnd(desiredMin, desiredMax);
        }
        float[] ds = new float[myCols];
        if (myConfig.smoothLandscape) {
            smooth(ds0, desiredMin, desiredMax, ds);
        } else {
            for (int i = 0; i < ds0.length; i++) {
                ds[i] = ds0[i];
            }
        }

        int nextD = (int) ds[0];
        for (int col = 0; col < myCols; col++) {
            int prevD = nextD;
            nextD = col == myCols - 1 ? (int) ds[0] : (int) ds[col];
            for (int row = 0; row < myRows; row++) {
                SurfaceDirection from = SurfaceDirection.FWD;
                SurfaceDirection to = SurfaceDirection.FWD;
                if (row < prevD) {
                    from = SurfaceDirection.DOWN;
                } else if (row > prevD) {
                    from = SurfaceDirection.UP;
                }
                if (row < nextD) {
                    to = SurfaceDirection.DOWN;
                } else if (row > nextD) {
                    to = SurfaceDirection.UP;
                }
                if (from == SurfaceDirection.DOWN && to == SurfaceDirection.DOWN) {
                    continue;
                }
                myMap[col][row] = myConfig.planetTiles.getGround(from, to);
            }
        }
        return myMap;
    }

    private void smooth(float[] ds0, float desiredMin, float desiredMax, float[] ds) {
        // smoothing
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (int x = 0; x < myCols; x++) {
            float prev = x == 0 ? ds0[myCols - 1] : ds0[x - 1];
            float next = x == myCols - 1 ? ds0[0] : ds0[x + 1];
            ds[x] = .5f * .5f * (prev + next) + .5f * ds0[x];
            if (ds[x] < min) {
                min = ds[x];
            }
            if (max < ds[x]) {
                max = ds[x];
            }
        }
        float shift = min - desiredMin;
        float mul = (desiredMax - .01f - desiredMin) / (max - min);
        for (int x = 0; x < myCols; x++) {
            ds[x] = mul * (ds[x] - shift);
        }
    }

    private void createDungeon() {
        int nodeCount = 3; // should depend on something
        for (int i = 0; i < nodeCount; i++) {
            int col = (int) (1f * myCols * i / nodeCount);
            buildNode(col);
        }
        for (int col = 0; col < myCols; col++) {
            for (int row = 0; row < myRows; row++) {
                if (!myDungeon[col][row]) {
                    continue;
                }
                myMap[col][row] = isGround(col, row) ? getDungeonTile(col, row) : getEntranceTile(col, row);
            }
        }
    }

    private Tile getEntranceTile(int col, int row) {
        boolean down = isGround(col, row + 1);
        boolean left = isGround(left(col), row);
        boolean right = isGround(right(col), row);
        return myConfig.planetTiles.getDungeonEntrance(down, left, right);
    }

    private Tile getDungeonTile(int col, int row) {
        return null;
    }

    private void buildNode(int col) {
        int row = myRows - SolMath.intRnd(0, PURE_GROUND_ROWS / 2);
        buildTunnel(col, row, true);
        buildTunnel(col, row, false);
    }

    private void buildTunnel(int col, int row, boolean toLeft) {
        float currSpace = 0f;
        addToDungeon(col, row);
        while (true) {
            int newCol = toLeft ? left(col) : right(col);
            //      if (!isCorner)
            col = newCol;
            currSpace += SolMath.rnd(.5f, SolMath.test(.3f) ? 4 : 1);
            if (addToDungeon(col, row)) {
                return;
            }
            while (currSpace > 0) {
                currSpace -= 1;
                row -= 1;
                if (addToDungeon(col, row)) {
                    return;
                }
            }
        }
    }

    private boolean addToDungeon(int col, int row) {
        myDungeon[col][row] = true;
        return !isGround(col, row);
    }

    private boolean isGround(int col, int row) {
        Tile t = myMap[col][row];
        return t != null && t.from == SurfaceDirection.UP && t.to == SurfaceDirection.UP;
    }

    private int left(int col) {
        return col == 0 ? myCols - 1 : col - 1;
    }

    private int right(int col) {
        return col == myCols - 1 ? 0 : col + 1;
    }
}
