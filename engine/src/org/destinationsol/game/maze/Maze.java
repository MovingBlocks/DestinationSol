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

import com.badlogic.gdx.math.Vector2;
import org.destinationsol.Const;
import org.destinationsol.game.HardnessCalc;
import org.destinationsol.game.SolCam;
import org.destinationsol.game.SolGame;

public class Maze {
    private final MazeConfig myConfig;
    private final Vector2 myPos;
    private final float myRadius;
    private final float myDps;
    private boolean myObjsCreated;

    public Maze(MazeConfig config, Vector2 pos, float radius) {
        myConfig = config;
        myPos = pos;
        myRadius = radius;
        myDps = HardnessCalc.getMazeDps(config);
    }

    public void update(SolGame game) {
        SolCam cam = game.getCam();
        Vector2 camPos = cam.getPos();
        if (!myObjsCreated && camPos.dst(myPos) < myRadius + Const.CAM_VIEW_DIST_JOURNEY * 2) {
            new MazeBuilder().build(game, this);
            myObjsCreated = true;
        }
    }

    public MazeConfig getConfig() {
        return myConfig;
    }

    public Vector2 getPos() {
        return myPos;
    }

    /**
     * @return the full radius including the exterior border.
     */
    public float getRadius() {
        return myRadius;
    }

    public float getDps() {
        return myDps;
    }
}
