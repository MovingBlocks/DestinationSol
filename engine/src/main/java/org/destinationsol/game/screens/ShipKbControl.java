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
package org.destinationsol.game.screens;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.game.Hero;
import org.destinationsol.game.item.Gun;
import org.destinationsol.ui.DisplayDimensions;
import org.destinationsol.ui.SolUiControl;

import java.util.List;

public class ShipKbControl implements ShipUiControl {
    public final SolUiControl leftCtrl;
    public final SolUiControl rightCtrl;
    public final SolUiControl upCtrl;
    public final SolUiControl myDownCtrl;
    public final SolUiControl shootCtrl;
    public final SolUiControl shoot2Ctrl;
    public final SolUiControl abilityCtrl;

    ShipKbControl(SolApplication solApplication, List<SolUiControl> controls) {
        DisplayDimensions displayDimensions = SolApplication.getInstance().getDisplayDimensions();

        GameOptions gameOptions = solApplication.getOptions();
        boolean showButtons = solApplication.isMobile();
        float col0 = 0;
        float col1 = col0 + MainGameScreen.CELL_SZ;
        float colN0 = displayDimensions.getRatio() - MainGameScreen.CELL_SZ;
        float colN1 = colN0 - MainGameScreen.CELL_SZ;
        float rowN0 = 1 - MainGameScreen.CELL_SZ;
        float rowN1 = rowN0 - MainGameScreen.CELL_SZ;

        leftCtrl = new SolUiControl(showButtons ? MainGameScreen.btn(colN1, rowN0, false) : null, false, gameOptions.getKeyLeft());
        leftCtrl.setDisplayName("Left");
        controls.add(leftCtrl);
        rightCtrl = new SolUiControl(showButtons ? MainGameScreen.btn(colN0, rowN0, false) : null, false, gameOptions.getKeyRight());
        rightCtrl.setDisplayName("Right");
        controls.add(rightCtrl);
        upCtrl = new SolUiControl(showButtons ? MainGameScreen.btn(col0, rowN0, false) : null, false, gameOptions.getKeyUp());
        upCtrl.setDisplayName("Fwd");
        controls.add(upCtrl);
        myDownCtrl = new SolUiControl(null, true, gameOptions.getKeyDown());
        controls.add(myDownCtrl);
        shootCtrl = new SolUiControl(showButtons ? MainGameScreen.btn(col0, rowN1, false) : null, false, gameOptions.getKeyShoot());
        shootCtrl.setDisplayName("Gun 1");
        controls.add(shootCtrl);
        shoot2Ctrl = new SolUiControl(showButtons ? MainGameScreen.btn(col1, rowN0, false) : null, false, gameOptions.getKeyShoot2());
        shoot2Ctrl.setDisplayName("Gun 2");
        controls.add(shoot2Ctrl);
        abilityCtrl = new SolUiControl(showButtons ? MainGameScreen.btn(colN0, rowN1, false) : null, false, gameOptions.getKeyAbility());
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
    }

    @Override
    public boolean isLeft() {
        return leftCtrl.isOn();
    }

    @Override
    public boolean isRight() {
        return rightCtrl.isOn();
    }

    @Override
    public boolean isUp() {
        return upCtrl.isOn();
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
