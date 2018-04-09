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

package org.destinationsol.game.maze;

import org.destinationsol.common.SolMath;
import org.destinationsol.common.SolRandom;

public class MazeLayoutBuilder {
    private static final float HOLE_PERCENTAGE = 0.2f;
    private static final float WALL_PERCENTAGE = 0.5f;
    private final int size;
    private final boolean[][] inners;
    private final boolean[][] holes;
    private final boolean[][] right;
    private final boolean[][] down;

    MazeLayoutBuilder(int size) {
        this.size = size;
        inners = new boolean[size][size];
        holes = new boolean[size][size];
        right = new boolean[size][size];
        down = new boolean[size][size];
    }

    public MazeLayout build() {
        setInners();
        for (int col = 0; col < size; col++) {
            for (int row = 0; row < size; row++) {
                boolean inner = inners[col][row];
                boolean rInner = col < size - 1 && inners[col + 1][row];
                boolean dInner = row < size - 1 && inners[col][row + 1];
                right[col][row] = (inner || rInner) && SolRandom.test(WALL_PERCENTAGE);
                down[col][row] = (inner || dInner) && SolRandom.test(WALL_PERCENTAGE);
            }
        }
        makeAllAccessible();
        return new MazeLayout(inners, holes, right, down);
    }

    private void makeAllAccessible() {
        int[][] steps = new int[size][size];
        expandPath(steps, 0, 0, 0);
        for (int col = 0; col < size; col++) {
            for (int row = 0; row < size; row++) {
                if (steps[col][row] != 0) {
                    continue;
                }
                int lStep = 0;
                if (col > 0) {
                    lStep = steps[col - 1][row];
                }
                int uStep = 0;
                if (row > 0) {
                    uStep = steps[col][row - 1];
                }
                int step;
                if (lStep < uStep) {
                    down[col][row - 1] = false;
                    step = uStep;
                } else {
                    right[col - 1][row] = false;
                    step = lStep;
                }
                expandPath(steps, col, row, step);
            }
        }
    }

    private void expandPath(int[][] steps, int col, int row, int prevStep) {
        if (steps[col][row] > 0) {
            return;
        }
        int step = prevStep + 1;
        steps[col][row] = step;
        if (col > 0 && !right[col - 1][row]) {
            expandPath(steps, col - 1, row, step);
        }
        if (row > 0 && !down[col][row - 1]) {
            expandPath(steps, col, row - 1, step);
        }
        if (col < size - 1 && !right[col][row]) {
            expandPath(steps, col + 1, row, step);
        }
        if (row < size - 1 && !down[col][row]) {
            expandPath(steps, col, row + 1, step);
        }
    }

    private void setInners() {
        float[][] values = new float[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                values[i][j] = SolRandom.seededRandomFloat(0, 1);
            }
        }
        smooth(values);
        smooth(values);
        float min = 1;
        float max = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                float value = values[i][j];
                if (max < value) {
                    max = value;
                }
                if (value < min) {
                    min = value;
                }
            }
        }
        float multiplier = 1 / (max - min);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                values[i][j] = multiplier * (values[i][j] - min);
            }
        }
        int value1 = 0;
        int value2 = 0;
        int value3 = 0;
        float chance1 = HOLE_PERCENTAGE - 0.05f;
        float chance2 = HOLE_PERCENTAGE;
        float chance3 = HOLE_PERCENTAGE + 0.05f;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (values[i][j] < chance1) {
                    value1++;
                }
                if (values[i][j] < chance2) {
                    value2++;
                }
                if (values[i][j] < chance3) {
                    value3++;
                }
            }
        }
        float p1 = 1f * value1 / size / size;
        float p2 = 1f * value2 / size / size;
        float p3 = 1f * value3 / size / size;
        p1 = SolMath.abs(p1 - HOLE_PERCENTAGE);
        p2 = SolMath.abs(p2 - HOLE_PERCENTAGE);
        p3 = SolMath.abs(p3 - HOLE_PERCENTAGE);
        float cutoff;
        if (p1 < p2) {
            cutoff = p1 < p3 ? chance1 : chance3;
        } else {
            cutoff = p2 < p3 ? chance2 : chance3;
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (isOk(i, j) && cutoff < values[i][j]) {
                    inners[i][j] = true;
                }
            }
        }
    }

    private void smooth(float[][] vals) {
        for (int col = 0; col < size; col++) {
            for (int row = 0; row < size; row++) {
                float v = vals[col][row];
                float lv = col == 0 ? 1 : vals[col - 1][row];
                float rv = col == size - 1 ? 1 : vals[col + 1][row];
                float uv = row == 0 ? 1 : vals[col][row - 1];
                float dv = row == size - 1 ? 1 : vals[col][row + 1];
                vals[col][row] = (v + lv + rv + uv + dv) / 5;
            }
        }
    }

    private boolean isOk(int i, int j) {
        int ii = i - size / 2;
        int jj = j - size / 2;
        float dist = SolMath.sqrt(ii * ii + jj * jj);
        return dist < size / 2;
    }
}
