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
import org.destinationsol.ui.responsiveUi.UiHeadlessButton;
import org.destinationsol.ui.responsiveUi.UiRelativeLayout;

public class ShipKbControl implements ShipUiControl {
    private UiHeadlessButton leftButton;
    private UiHeadlessButton rightButton;
    private UiHeadlessButton upButton;
    private UiHeadlessButton downButton;
    private UiHeadlessButton shootButton;
    private UiHeadlessButton shoot2Button;
    private UiHeadlessButton abilityButton;

    ShipKbControl(SolApplication solApplication, UiRelativeLayout rootUiElement) {
        GameOptions gameOptions = solApplication.getOptions();

        // TODO: Make the buttons visible for mobile.
        /*
        boolean showButtons = solApplication.isMobile();
        if (showButtons) {
            leftButton = new UiTextButton(...);
        } else {
            leftButton = new UiHeadlessButton(...);
        }
        */

        leftButton = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyLeft());
        rootUiElement.addHeadlessElement(leftButton);

        rightButton = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyRight());
        rootUiElement.addHeadlessElement(rightButton);

        upButton = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyUp());
        rootUiElement.addHeadlessElement(upButton);

        downButton = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyDown());
        rootUiElement.addHeadlessElement(downButton);

        shootButton = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyShoot());
        rootUiElement.addHeadlessElement(shootButton);

        shoot2Button = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyShoot2());
        rootUiElement.addHeadlessElement(shoot2Button);

        abilityButton = new UiHeadlessButton().setTriggerKey(gameOptions.getKeyAbility());
        rootUiElement.addHeadlessElement(abilityButton);
    }

    @Override
    public void update(SolApplication solApplication, boolean enabled) {
        if (!enabled) {
            leftButton.setEnabled(false);
            rightButton.setEnabled(false);
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            shootButton.setEnabled(false);
            shoot2Button.setEnabled(false);
            abilityButton.setEnabled(false);
            return;
        }

        Hero hero = solApplication.getGame().getHero();
        boolean hasEngine = hero.isNonTranscendent() && hero.getHull().getEngine() != null;
        leftButton.setEnabled(hasEngine);
        rightButton.setEnabled(hasEngine);
        upButton.setEnabled(hasEngine);
        downButton.setEnabled(hasEngine);

        Gun gun1 = hero.isTranscendent() ? null : hero.getHull().getGun(false);
        shootButton.setEnabled(gun1 != null && gun1.ammo > 0);
        Gun gun2 = hero.isTranscendent() ? null : hero.getHull().getGun(true);
        shoot2Button.setEnabled(gun2 != null && gun2.ammo > 0);

        abilityButton.setEnabled(hero.isNonTranscendent() && hero.canUseAbility());
    }

    @Override
    public boolean isLeft() {
        return leftButton.isOn();
    }

    @Override
    public boolean isRight() {
        return rightButton.isOn();
    }

    @Override
    public boolean isUp() {
        return upButton.isOn();
    }

    @Override
    public boolean isDown() {
        return downButton.isOn();
    }

    @Override
    public boolean isShoot() {
        return shootButton.isOn();
    }

    @Override
    public boolean isShoot2() {
        return shoot2Button.isOn();
    }

    @Override
    public boolean isAbility() {
        return abilityButton.isOn();
    }
}
