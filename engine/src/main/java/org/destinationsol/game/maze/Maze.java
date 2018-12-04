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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.game.HardnessCalc;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;

public class Maze {
    private final MazeConfig config;
    private final Vector2 position;
    private final float radius;
    private final float damagePerSecond;
    private boolean areObjectsCreated;

    public Maze(MazeConfig config, Vector2 position, float radius) {
        this.config = config;
        this.position = position;
        this.radius = radius;
        damagePerSecond = HardnessCalc.getMazeDps(config);
    }

    public void update(SolGame game) {
        SolCam cam = game.getCam();
        Vector2 camPos = cam.getPosition();
        if (!areObjectsCreated && camPos.dst(position) < radius + Const.CAM_VIEW_DIST_JOURNEY * 2) {
            new MazeBuilder().build(game, this);
            areObjectsCreated = true;
        }
    }

    public MazeConfig getConfig() {
        return config;
    }

    public Vector2 getPos() {
        return position;
    }

    /**
     * @return the full radius including the exterior border.
     */
    public float getRadius() {
        return radius;
    }

    public float getDps() {
        return damagePerSecond;
    }
}
