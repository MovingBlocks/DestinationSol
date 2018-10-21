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
package org.destinationsol.game.input;

import org.destinationsol.Const;
import org.destinationsol.game.Faction;
import org.destinationsol.game.SolGame;
import org.destinationsol.game.screens.MainGameScreen;
import org.destinationsol.game.ship.FarShip;
import org.destinationsol.game.ship.SolShip;

public class UiControlledPilot implements Pilot {
    private final MainGameScreen myScreen;

    public UiControlledPilot(MainGameScreen screen) {
        myScreen = screen;
    }

    @Override
    public void update(SolGame game, SolShip ship, SolShip nearestEnemy) { }

    @Override
    public boolean isUp() {
        return myScreen.isUp();
    }

    @Override
    public boolean isLeft() {
        return myScreen.isLeft();
    }

    @Override
    public boolean isRight() {
        return myScreen.isRight();
    }

    @Override
    public boolean isShoot() {
        return myScreen.isShoot();
    }

    @Override
    public boolean isShoot2() {
        return myScreen.isShoot2();
    }

    @Override
    public boolean collectsItems() {
        return true;
    }

    @Override
    public boolean isAbility() {
        return myScreen.isAbility();
    }

    @Override
    public Faction getFaction() {
        return Faction.LAANI;
    }

    @Override
    public void stringToFaction(String faction) {
    // TODO Create values outside of laani and ehar, making this necessary
    }

    @Override
    public boolean shootsAtObstacles() {
        return false;
    }

    @Override
    public float getDetectionDist() {
        return Const.AUTO_SHOOT_SPACE; // just for unfixed mounts
    }

    @Override
    public String getMapHint() {
        return "You";
    }

    @Override
    public void updateFar(SolGame game, FarShip farShip) { }

    @Override
    public String toDebugString() {
        return "";
    }

    @Override
    public boolean isPlayer() {
        return true;
    }
}
