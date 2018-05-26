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
package org.destinationsol.game;

import org.destinationsol.Const;
import org.destinationsol.game.ship.ShipAbility;
import org.destinationsol.game.ship.SloMo;
import org.destinationsol.ui.DebugCollector;

public class TimeProvider {
    private float timeFactor = DebugOptions.GAME_SPEED_MULTIPLIER;
    private float timeStep = Const.REAL_TIME_STEP;
    private Hero hero;
    private float time;
    private boolean paused;

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        DebugCollector.warn(this.paused ? "game paused" : "game resumed");
    }

    public float getTime() {
        return time;
    }

    public void update() {
        timeFactor = DebugOptions.GAME_SPEED_MULTIPLIER;
        if (hero.isAlive() && hero.isNonTranscendent()) {
            ShipAbility ability = hero.getAbility();
            if (ability instanceof SloMo) {
                float factor = ((SloMo) ability).getFactor();
                timeFactor *= factor;
            }
        }
        timeStep = Const.REAL_TIME_STEP * timeFactor;
        time += timeStep;
    }

    public float getTimeFactor() {
        return timeFactor;
    }

    public float getTimeStep() {
        return timeStep;
    }

    // Hack for now while still porting, later should be handled by callbacks
    public void setTrackingHero(Hero hero) {
        this.hero = hero;
    }
}
