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
