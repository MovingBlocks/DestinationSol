/*
 * Copyright 2021 The Terasology Foundation
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
package org.destinationsol.ui.nui.screens;

import org.destinationsol.GameOptions;
import org.destinationsol.SolApplication;
import org.destinationsol.common.In;
import org.destinationsol.game.Hero;
import org.destinationsol.game.item.Gun;
import org.destinationsol.game.screens.ShipUiControl;
import org.destinationsol.ui.nui.NUIScreenLayer;
import org.destinationsol.ui.nui.widgets.KeyActivatedButton;
import org.destinationsol.ui.nui.widgets.UIWarnButton;
import org.terasology.input.ButtonState;
import org.terasology.input.Keyboard;
import org.terasology.nui.AbstractWidget;
import org.terasology.nui.backends.libgdx.GDXInputUtil;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.nui.widgets.UIButton;

/**
 * This screen contains the touchscreen controls used when playing the game on mobile platforms.
 * It is the implementation of {@link ShipUiControl} used when the control type is set to "Keyboard".
 */
public class UIShipControlsScreen extends NUIScreenLayer implements ShipUiControl {
    public UIWarnButton leftButton;
    public UIWarnButton rightButton;
    public UIWarnButton forwardButton;
    public UIWarnButton gun1Button;
    public UIWarnButton gun2Button;
    public UIWarnButton abilityButton;
    private Keyboard.Key downKey;
    private boolean downKeyHeld;
    private boolean controlsEnabled;

    @In
    private SolApplication solApplication;

    @Override
    public void initialise() {
        GameOptions gameOptions = solApplication.getOptions();

        leftButton = find("leftButton", UIWarnButton.class);
        leftButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyLeft()));

        rightButton = find("rightButton", UIWarnButton.class);
        rightButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyRight()));

        forwardButton = find("forwardButton", UIWarnButton.class);
        forwardButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyUp()));

        gun1Button = find("gun1Button", UIWarnButton.class);
        gun1Button.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyShoot()));

        gun2Button = find("gun2Button", UIWarnButton.class);
        gun2Button.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyShoot2()));

        abilityButton = find("abilityButton", UIWarnButton.class);
        abilityButton.setKey(GDXInputUtil.GDXToNuiKey(gameOptions.getKeyAbility()));

        downKey = GDXInputUtil.GDXToNuiKey(gameOptions.getKeyDown());

        // Hide touchscreen controls if not on mobile
        if (!solApplication.isMobile()) {
            leftButton.setVisible(false);
            rightButton.setVisible(false);
            forwardButton.setVisible(false);
            gun1Button.setVisible(false);
            gun2Button.setVisible(false);
            abilityButton.setVisible(false);
        }
    }

    /**
     * Implementation of {@link org.terasology.nui.UIWidget#update(float)}
     * @param delta the time elapsed since the last update cycle
     */
    @Override
    public void update(float delta) {
        // Hide and disable controls if the main game screen is not visible.
        boolean mainGameScreenVisible = solApplication.getInputManager().isScreenOn(solApplication.getGame().getScreens().oldMainGameScreen);
        ((AbstractWidget)contents).setVisible(mainGameScreenVisible);
        contents.setEnabled(mainGameScreenVisible);

        if (!controlsEnabled) {
            leftButton.setEnabled(false);
            rightButton.setEnabled(false);
            forwardButton.setEnabled(false);
            gun1Button.setEnabled(false);
            gun2Button.setEnabled(false);
            abilityButton.setEnabled(false);
            return;
        }

        Hero hero = solApplication.getGame().getHero();
        boolean hasEngine = hero.isNonTranscendent() && hero.getHull().getEngine() != null;
        forwardButton.setEnabled(hasEngine);
        leftButton.setEnabled(hasEngine);
        rightButton.setEnabled(hasEngine);

        Gun gun1 = hero.isTranscendent() ? null : hero.getHull().getGun(false);
        boolean gun1Enabled = gun1 != null && gun1.ammo > 0;
        gun1Button.setEnabled(gun1Enabled);

        Gun gun2 = hero.isTranscendent() ? null : hero.getHull().getGun(true);
        boolean gun2Enabled = gun2 != null && gun2.ammo > 0;
        gun2Button.setEnabled(gun2Enabled);

        // The ability button needs to de-press before it is disabled,
        // as otherwise it causes the button to be pressed again when it is enabled.
        if (!abilityButton.getMode().equals(UIButton.DOWN_MODE)) {
            abilityButton.setEnabled(hero.isNonTranscendent() && hero.canUseAbility());
        }

        super.update(delta);
    }

    /**
     * Implementation of {@link ShipUiControl#update(SolApplication, boolean)}
     * @param solApplication an instance of the game's {@link SolApplication}
     * @param enabled are the UI controls currently enabled
     */
    @Override
    public void update(SolApplication solApplication, boolean enabled) {
        controlsEnabled = enabled;
    }

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        if (event.getKey() == downKey) {
            downKeyHeld = (event.getState() == ButtonState.DOWN);
            return true;
        }

        return super.onKeyEvent(event);
    }

    /**
     * Returns the "Left" touchscreen control button element.
     * @return the "Left" UI Button
     */
    public UIWarnButton getLeftButton() {
        return leftButton;
    }

    /**
     * Returns the "Right" touchscreen control button element.
     * @return the "Right" UI Button
     */
    public UIWarnButton getRightButton() {
        return rightButton;
    }

    /**
     * Returns the "Fwd" touchscreen control button element.
     * @return the "Fwd" UI Button
     */
    public UIWarnButton getForwardButton() {
        return forwardButton;
    }

    /**
     * Returns the "Gun" touchscreen control button element.
     * @return the "Gun" UI Button
     */
    public UIWarnButton getGun1Button() {
        return gun1Button;
    }

    /**
     * Returns the "Gun2" touchscreen control button element.
     * @return the "Gun2" UI Button
     */
    public UIWarnButton getGun2Button() {
        return gun2Button;
    }

    /**
     * Returns the "Ability" touchscreen control button element.
     * @return the "Ability" UI Button
     */
    public UIWarnButton getAbilityButton() {
        return abilityButton;
    }

    @Override
    public boolean isLeft() {
        return leftButton.getMode().equals(UIButton.DOWN_MODE);
    }

    @Override
    public boolean isRight() {
        return rightButton.getMode().equals(UIButton.DOWN_MODE);
    }

    @Override
    public boolean isUp() {
        return forwardButton.getMode().equals(UIButton.DOWN_MODE);
    }

    @Override
    public boolean isDown() {
        return downKeyHeld;
    }

    @Override
    public boolean isShoot() {
        return gun1Button.getMode().equals(UIButton.DOWN_MODE);
    }

    @Override
    public boolean isShoot2() {
        return gun2Button.getMode().equals(UIButton.DOWN_MODE);
    }

    @Override
    public boolean isAbility() {
        return abilityButton.getMode().equals(UIButton.DOWN_MODE);
    }

    @Override
    protected boolean escapeCloses() {
        return false;
    }
}
