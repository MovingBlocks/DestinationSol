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
package org.destinationsol.game.screens;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.SolMath;
import org.destinationsol.game.Hero;
import org.destinationsol.game.item.Gun;
import org.destinationsol.ui.SolUiControl;

import java.util.List;

public class ShipKbControl implements ShipUiControl {
    private static final float INITIAL_THROTTLE_INCREMENT_SPEED = 0.2f;
    private static final float INITIAL_ORIENTATION_INCREMENT_SPEED = 3f * SolMath.radDeg;
    private static final float MAX_ORIENTATION_INCREMENT_SPEED = 4f * SolMath.radDeg;
    private static final float MIN_ORIENTATION_CLAMP_DIFFERENCE = 0.1f * SolMath.radDeg;

    public final SolUiControl leftCtrl;
    public final SolUiControl rightCtrl;
    public final SolUiControl upCtrl;
    public final SolUiControl myDownCtrl;
    public final SolUiControl shootCtrl;
    public final SolUiControl shoot2Ctrl;
    public final SolUiControl abilityCtrl;

    private float upControlOnTime;
    private float downControlOnTime;
    private float leftControlOnTime;
    private float rightControlOnTime;

    private float throttle;
    private float orientation;

    ShipKbControl(SolApplication solApplication, float resolutionRatio, List<SolUiControl> controls) {
        GameOptions gameOptions = solApplication.getOptions();
        boolean showButtons = solApplication.isMobile();
        float col0 = 0;
        float col1 = col0 + MainScreen.CELL_SZ;
        float colN0 = resolutionRatio - MainScreen.CELL_SZ;
        float colN1 = colN0 - MainScreen.CELL_SZ;
        float rowN0 = 1 - MainScreen.CELL_SZ;
        float rowN1 = rowN0 - MainScreen.CELL_SZ;

        leftCtrl = new SolUiControl(showButtons ? MainScreen.btn(colN1, rowN0, false) : null, false, gameOptions.getKeyLeft());
        leftCtrl.setDisplayName("Left");
        controls.add(leftCtrl);
        rightCtrl = new SolUiControl(showButtons ? MainScreen.btn(colN0, rowN0, false) : null, false, gameOptions.getKeyRight());
        rightCtrl.setDisplayName("Right");
        controls.add(rightCtrl);
        upCtrl = new SolUiControl(showButtons ? MainScreen.btn(col0, rowN0, false) : null, false, gameOptions.getKeyUp());
        upCtrl.setDisplayName("Fwd");
        controls.add(upCtrl);
        myDownCtrl = new SolUiControl(null, true, gameOptions.getKeyDown());
        controls.add(myDownCtrl);
        shootCtrl = new SolUiControl(showButtons ? MainScreen.btn(col0, rowN1, false) : null, false, gameOptions.getKeyShoot());
        shootCtrl.setDisplayName("Gun 1");
        controls.add(shootCtrl);
        shoot2Ctrl = new SolUiControl(showButtons ? MainScreen.btn(col1, rowN0, false) : null, false, gameOptions.getKeyShoot2());
        shoot2Ctrl.setDisplayName("Gun 2");
        controls.add(shoot2Ctrl);
        abilityCtrl = new SolUiControl(showButtons ? MainScreen.btn(colN0, rowN1, false) : null, false, gameOptions.getKeyAbility());
        abilityCtrl.setDisplayName("Ability");
        controls.add(abilityCtrl);
    }

    @Override
    public void update(SolApplication solApplication, boolean enabled) {
        if (!enabled) {
            upCtrl.setEnabled(false);
            leftCtrl.setEnabled(false);
            rightCtrl.setEnabled(false);
            shootCtrl.setEnabled(false);
            shoot2Ctrl.setEnabled(false);
            abilityCtrl.setEnabled(false);
            return;
        }
        Hero hero = solApplication.getGame().getHero();
        boolean hasEngine = hero.isNonTranscendent() && hero.getHull().getEngine() != null;
        upCtrl.setEnabled(hasEngine);
        leftCtrl.setEnabled(hasEngine);
        rightCtrl.setEnabled(hasEngine);

        Gun g1 = hero.isTranscendent() ? null : hero.getHull().getGun(false);
        shootCtrl.setEnabled(g1 != null && g1.ammo > 0);
        Gun g2 = hero.isTranscendent() ? null : hero.getHull().getGun(true);
        shoot2Ctrl.setEnabled(g2 != null && g2.ammo > 0);
        abilityCtrl.setEnabled(hero.isNonTranscendent() && hero.canUseAbility());

        float timeStep = solApplication.getGame().getTimeStep();

        if (upCtrl.isOn()) {
            throttle += getIncrementForOnTime(timeStep, upControlOnTime, INITIAL_THROTTLE_INCREMENT_SPEED);
            upControlOnTime += timeStep;
        } else if (upCtrl.isJustOff()) {
            upControlOnTime = 0;
        }

        if (myDownCtrl.isOn()) {
            throttle -= getIncrementForOnTime(timeStep, downControlOnTime, INITIAL_THROTTLE_INCREMENT_SPEED);
            downControlOnTime += timeStep;
        } else if (myDownCtrl.isJustOff()) {
            downControlOnTime = 0;
        }

        throttle = SolMath.clamp(throttle);

        if (SolMath.angleDiff(orientation, solApplication.getGame().getHero().getAngle()) >= 3f * SolMath.radDeg) {
            return;
        }

        if (leftCtrl.isOn()) {
            orientation -= Math.min(
                    getIncrementForOnTime(timeStep, leftControlOnTime, INITIAL_ORIENTATION_INCREMENT_SPEED),
                    MAX_ORIENTATION_INCREMENT_SPEED * timeStep
            );
            leftControlOnTime += timeStep;
        } else if (leftCtrl.isJustOff()) {
            clampOrientation(solApplication.getGame().getHero().getAngle());

            leftControlOnTime = 0;
        }

        if (rightCtrl.isOn()) {
            orientation += Math.min(
                    getIncrementForOnTime(timeStep, rightControlOnTime, INITIAL_ORIENTATION_INCREMENT_SPEED),
                    MAX_ORIENTATION_INCREMENT_SPEED * timeStep
            );
            rightControlOnTime += timeStep;
        } else if (rightCtrl.isJustOff()) {
            clampOrientation(solApplication.getGame().getHero().getAngle());

            rightControlOnTime = 0;
        }

        orientation = SolMath.norm(orientation);
    }

    private void clampOrientation(float shipOrientation) {
        shipOrientation = SolMath.norm(shipOrientation);
        if (SolMath.norm(orientation - shipOrientation) >= MIN_ORIENTATION_CLAMP_DIFFERENCE) {
            orientation = shipOrientation + MIN_ORIENTATION_CLAMP_DIFFERENCE;
        }
    }

    private float getIncrementForOnTime(float timeStep, float onTime, float initialIncrementSpeed) {
        return (float) (initialIncrementSpeed * (1 + Math.pow(onTime, 3.5)) * timeStep);
    }

    @Override
    public float getThrottle() {
        return throttle;
    }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public boolean isDown() {
        return myDownCtrl.isOn();
    }

    @Override
    public boolean isShoot() {
        return shootCtrl.isOn();
    }

    @Override
    public boolean isShoot2() {
        return shoot2Ctrl.isOn();
    }

    @Override
    public boolean isAbility() {
        return abilityCtrl.isOn();
    }
}
